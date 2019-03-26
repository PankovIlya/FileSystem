package System;

import jdk.net.SocketFlow;
import org.junit.Ignore;
import org.junit.Test;
import system.MemoryFileSystem;
import system.data.Node;
import system.data.Status;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


public class TestFileSystem {

    @Test
    public void testCreateDirSimple() {
        MemoryFileSystem system = new MemoryFileSystem(256);
        createDir(system);
    }

    @Test
    public void testCreateFileSimple() {
        MemoryFileSystem system = new MemoryFileSystem(256);
        testCreateFileSimple(system);
    }

    @Test
    public void testDeleteFileSimple() {
        MemoryFileSystem system = new MemoryFileSystem(256);
        Status<Node> status = testCreateFileSimple(system);
        Node node = status.getData();
        Status statusDel = system.deleteFile(node.getFullName());
        assert statusDel.code == 0;
    }

    @Test
    public void testDeleteDirSimple() {
        MemoryFileSystem system = new MemoryFileSystem(256);
        Status<Node> status = testCreateFileSimple(system);
        Node node = status.getData();
        assert node != null;
        Status statusDel = system.deleteDir(node.getPath());
        assert statusDel.code == 0;
    }

    private Status<Node> testCreateFileSimple(MemoryFileSystem system) {
        Status<Node> status = createDir(system);
        Node node = status.getData();
        String fileName = "test";
        Status<Node> statusFile = system.addFile(node.getFullName(), fileName);
        assert status.code == 0;
        return statusFile;
    }

    private Status<Node> createDir(MemoryFileSystem system) {
        String name = "test";
        String path = "/";
        Status<Node> status = system.addDir(path, name);
        assert status.code == 0;
        return status;
    }

    @Test
    public void testFindFileDelFile() {
        MemoryFileSystem system = new MemoryFileSystem(256);
        for (int i = 0; i < 10; i++) {
            Status<Node> status = system.addDir("/", "name" + i);
            final Deque<Node> deque = new LinkedList<>();
            deque.addFirst(status.getData());
            for (int j = 0; j < 10; j++) {
                Status<Node> status1 = system.addDir(deque.pop().getFullName(), "name" + i + j);
                Node node = status1.getData();
                system.addFile(node.getFullName(), "file" + i + j);
                deque.addFirst(node);
            }
        }

        Status<List<Node>> findFile = system.findFile("file55", true);
        assert findFile.code == 0;
        assert findFile.getData().size() == 1;

        Node file = findFile.getData().get(0);
        Status delFile = system.deleteFile(file.getFullName());
        assert delFile.code == 0;

        Status<List<Node>> findFile2 = system.findFile("file55", true);
        assert findFile2.code == 0;
        assert findFile2.getData().isEmpty();


    }

    @Test
    public void testFindFileDelDir() {
        MemoryFileSystem system = new MemoryFileSystem(256);
        for (int i = 0; i < 10; i++) {
            Status<Node> status = system.addDir("/", "name" + i);
            final Deque<Node> deque = new LinkedList<>();
            deque.add(status.getData());
            for (int j = 0; j < 10; j++) {
                Status<Node> status1 = system.addDir(deque.pop().getFullName(), "name" + i + j);
                Node node = status1.getData();
                system.addFile(node.getFullName(), "file" + i + j);
                deque.addFirst(node);
            }
        }

        Status<List<Node>> findDir = system.findDir("name22", true);
        assert findDir.code == 0;
        assert findDir.getData().size() == 1;
        Node dir = findDir.getData().get(0);

        Status<List<Node>> findFile = system.findFile("file23", true);
        assert findFile.code == 0;
        assert findFile.getData().size() == 1;

        Status delDir = system.deleteDir(dir.getFullName());
        assert delDir.code == 0;

        Status<List<Node>> findFile2 = system.findFile("file23", true);
        assert findFile2.code == 0;
        assert findFile2.getData().isEmpty();
    }

    @Test
    public void testFindDirDelDir() {
        MemoryFileSystem system = new MemoryFileSystem(256);
        for (int i = 0; i < 10; i++) {
            Status<Node> status = system.addDir("/", "name" + i);
            final Deque<Node> deque = new LinkedList<>();
            deque.add(status.getData());
            for (int j = 0; j < 10; j++) {
                Status<Node> status1 = system.addDir(deque.pop().getFullName(), "name" + i + j);
                Node node = status1.getData();
                system.addFile(node.getFullName(), "file" + i + j);
                deque.addFirst(node);
            }
        }

        Status<List<Node>> findParent = system.findDir("name22", true);
        assert findParent.code == 0;
        assert findParent.getData().size() == 1;
        Node dir = findParent.getData().get(0);

        Status<List<Node>> findDir = system.findDir("name23", true);
        assert findDir.code == 0;
        assert findDir.getData().size() == 1;

        Status delDir = system.deleteDir(dir.getFullName());
        assert delDir.code == 0;

        Status<List<Node>> findDir22 = system.findDir("name22", true);
        assert findDir22.code == 0;
        assert findDir22.getData().size() == 0;

        Status<List<Node>> findDir23 = system.findDir("name23", true);
        assert findDir23.code == 0;
        assert findDir23.getData().size() == 0;
    }

}
