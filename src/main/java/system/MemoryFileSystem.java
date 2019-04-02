package system;

import system.data.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static system.data.ErrorCode.*;

public class MemoryFileSystem implements IMemoryFileSystem {

    final int MAX_LENGTH_NAME;
    final public static String SPACE = "/";

    private final Dir ROOT = new Dir("/", "");

    private final LinkedHashMap<INode, INode> iNodes = new LinkedHashMap<>();

    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();

    public MemoryFileSystem(int MAX_LENGTH_NAME) {
        this.MAX_LENGTH_NAME = MAX_LENGTH_NAME;
        iNodes.put(ROOT, ROOT);
    }

    @Override
    public Status<Node> addDir(String path, String name) {
        return handelException(() -> {
            if (!checkName(name)) {
                return new Status<>(NOT_VALID_NAME);
            }

            Dir newDir = new Dir(path, name);
            return addNode(newDir, true);
        });
    }


    @Override
    public Status<Node> addFile(String path, String name) {
        return handelException(() -> {
            if (!checkName(name)) {
                return new Status<>(NOT_VALID_NAME);
            }

            File newFile = new File(path, name);
            return addNode(newFile, false);

        });
    }

    @Override
    public Status<List<Node>> findDir(String name, boolean findAll) {
        if (!checkName(name)) {
            return new Status<>(NOT_VALID_NAME);
        }
        return findNode(ROOT, name, findAll, true, true);
    }

    @Override
    public Status<List<Node>> findDir(String path, String name, boolean findAll, boolean subDir) {
        return findAndCheckNode(path, name, findAll, subDir, true);
    }

    @Override
    public Status<List<Node>> findFile(String name, boolean findAll) {
        if (!checkName(name)) {
            return new Status<>(NOT_VALID_NAME);
        }
        return findNode(ROOT, name, findAll, true, false);
    }

    @Override
    public Status<List<Node>> findFile(String path, String name, boolean findAll, boolean subDir) {
        return findAndCheckNode(path, name, findAll, subDir, false);
    }

    @Override
    public Status deleteDir(String path) {
        return handelException(() ->  checkAndDeleteDir(getKey(path)));
    }

    @Override
    public Status deleteFile(String path) {
        return handelException(() -> deleteFile(getFile(path), path));
    }

    private Status<Node> addNode(INode node, boolean isDir) {
        beginWrite();
        try {

            INode current = exists(node);
            if (current != null) {
                return new Status<>(FILE_ALREADY_EXIST, current.getFullName());
            }

            Dir parent = (Dir) exists(getKey(node.getPath()));
            if (parent == null) {
                return new Status<>(DIR_NOT_FOUND, node.getPath());
            }

            if (isDir) {
                node.setSibling(parent.getChild());
                parent.setChild((Dir) node);

            } else {
                node.setSibling(parent.getFile());
                parent.setFile((File) node);
            }

            iNodes.put(node, node);

            return new Status<>(OK, new Node(node));
        } finally {
            endWrite();
        }
    }


    private Status<List<Node>> findAndCheckNode(String path, String name, boolean findAll, boolean subDir, boolean isDir) {
        return handelException(() -> {

            if (!checkName(name)) {
                return new Status<>(NOT_VALID_NAME);
            }
            Dir parent = (Dir) exists(getKey(path));
            if (parent == null) {
                return new Status<>(DIR_NOT_FOUND, path);
            }

            return findNode(parent, name, findAll, subDir, false);
        });
    }

    private Status checkAndDeleteDir(Dir node) {
        beginWrite();
        try {

            Dir dir = (Dir) exists(node);
            if (dir == null) {
                return new Status<>(DIR_NOT_FOUND, node.getFullName());
            }

            if (dir.equals(ROOT)) {
                return new Status<>(IS_ROOT);
            }

            if (exists(getKey(dir.getPath())) == null) {
                deleteAllNode(dir);
                return new Status<>(DIR_NOT_FOUND, node.getPath());
            }

            deleteDir(dir);
            return new Status<>(OK);

        } finally {
            endWrite();
        }
    }

    private void deleteDir(Dir dir) {
        if (dir == null) {
            return;
        }

        Dir parent = (Dir) exists(getKey(dir.getPath()));
        if (parent == null) {
            return;
        }

        deleteAllFile(dir);
        parent.setChild(dir.getSibling());
        deleteAllNode(dir.getChild());
        iNodes.remove(dir);
    }

    private void deleteAllNode(Dir dir) {
        if (dir == null) {
            return;
        }
        final Deque<Dir> queue = new LinkedList<>();
        queue.addFirst(dir);

        while (!queue.isEmpty()) {
            Dir current = queue.pop();
            if (current.getSibling() != null)
                queue.addFirst(current.getSibling());
            if (current.getChild() != null)
                queue.addLast(current.getChild());
            deleteNode(current);
        }
    }

    private void deleteAllFile(Dir dir) {
        if (dir == null) {
            return;
        }
        File file;
        while (dir.getFile() != null) {
            file = dir.getFile();
            file.free();
            dir.setFile(file.getSibling());
            iNodes.remove(file);
        }
    }

    private void deleteNode(Dir dir) {
        if (dir == null) {
            return;
        }
        deleteAllFile(dir);
        dir.setSibling(null);
        dir.setChild(null);
        iNodes.remove(dir);
    }

    private Status<List<Node>> findNode(Dir parent, String name, boolean findAll, boolean subDir, boolean isDir) {
        return handelException(() -> {
            List<Node> result = findAllNode(parent, name, findAll, subDir, isDir? 0: 1);
            return new Status<>(OK, result);
        });
    }

    private List<Node> findAllNode(Dir dir, String name, boolean findAll, boolean subDir, int isDir) {
        beginRead();
        try {
            final Deque<Dir> queue = new LinkedList<>();
            final List<Node> results = new ArrayList<>();
            queue.addFirst(dir);

            while (!queue.isEmpty()) {
                if (!findAll || !results.isEmpty())
                    break;

                Dir current = queue.pop();

                if (current.getSibling() != null)
                    queue.addFirst(current.getSibling());

                if (subDir && current.getChild() != null)
                    queue.addLast(current.getChild());

                INode node = new INode(current.getPath(), name, isDir);

                INode res = getINode(node);
                if (res != null)
                    results.add(new Node(res));
            }

            return results;
        } finally {
            endRead();
        }
    }

    private Status deleteFile(File file, String path) {
        beginWrite();
        try {
            if (file == null) {
                return new Status<>(FILE_NOT_FOUND, path);
            }

            File old = (File) iNodes.remove(file);
            if (old == null) {
                return new Status<>(FILE_NOT_FOUND, path);
            }

            old.free();

            Dir dir = (Dir) exists(getKey(old.getPath()));
            if (dir == null) {
                return new Status<>(FILE_NOT_FOUND, old.getPath());
            }

            dir.setFile(old.getSibling());

            return new Status<>(OK);
        } finally {
            endWrite();
        }
    }

    private INode exists(INode node) {
        this.beginRead();
        try {
            return getINode(node);
        } finally {
            this.endRead();
        }
    }

    private INode getINode(INode node) {
        if (node == null)
            return null;
        return iNodes.get(node);
    }


    private final void beginWrite() {
        this.rwlock.writeLock().lock();
    }

    private final void beginRead() {
        this.rwlock.readLock().lock();
    }

    private final void endRead() {
        this.rwlock.readLock().unlock();
    }

    private final void endWrite() {
        this.rwlock.writeLock().unlock();
    }

    private Dir getKey(String path) {
        return (Dir) getINode(path, true);
    }

    private File getFile(String path) {
        return (File) getINode(path, false);
    }

    private INode getINode(String path, boolean isDir) {
        if (ROOT.getPath().equals(path)) {
            return ROOT;
        }
        int i = path.lastIndexOf(SPACE);
        if (i == -1) {
            return null;
        }
        String parentPath;
        if (path.substring(0, i).isEmpty())
            parentPath = ROOT.getPath();
        else
            parentPath = path.substring(0, i);

        if (isDir)
            return new Dir(parentPath, path.substring(i + 1));

        return new File(parentPath, path.substring(i + 1));
    }

    private boolean checkName(String name) {
        return !(name == null || name.isEmpty() || name.contains(SPACE) || name.length() > this.MAX_LENGTH_NAME);
    }

    private <T> Status<T> handelException(Callable< Status<T>> call){
        try {
            return call.call();
        } catch (Exception e) {
            return new Status<>(UNKNOWN_ERROR, e.getMessage());
        }
    }

}
