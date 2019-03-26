package system;

import system.data.INode;
import system.data.Node;
import system.data.Status;

import java.util.List;

public interface IMemoryFileSystem {

    Status<Node> addDir(String path, String name);

    Status<Node> addFile(String path, String name);

    Status deleteDir(String path);

    Status deleteFile(String path);
    /**
     * full scan
     *
     * @param name dir name
     * @param findAll if false return when find first equals name
     * @return list dir
     */
    Status<List<Node>> findDir(String name, boolean findAll);

    /**
     *
     * @param path path for search
     * @param name dir name
     * @param findAll if false return when find first equals name
     * @param subDir include sub dir
     * @return list dir
     */
    Status<List<Node>> findDir(String path, String name, boolean findAll, boolean subDir);

    /**
     * full scan
     *
     * @param name file name
     * @param findAll if false return when find first equals name
     * @return list dir
     */
    Status<List<Node>> findFile(String name, boolean findAll);

    /**
     *
     * @param path path for search
     * @param name file name
     * @param findAll if false return when find first equals name
     * @param subDir include sub dir
     * @return list dir
     */
    Status<List<Node>> findFile(String path, String name, boolean findAll, boolean subDir);
}
