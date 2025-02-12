package com.jetbrains.jacobfs.functionaltests;

import com.jetbrains.jacobfs.command.DeleteFile;
import com.jetbrains.jacobfs.command.ListAllFiles;
import com.jetbrains.jacobfs.command.WriteFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Timeout.ThreadMode.SEPARATE_THREAD;

@Disabled
class PerformanceTest extends AbstractFunctionalTest {

    RandomStringUtils insecure = RandomStringUtils.insecure();

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS, threadMode = SEPARATE_THREAD)
    void writeDelete() throws IOException {
        int testFilesAmount = 8192;
        ArrayList<WriteFile> writeFiles = new ArrayList<>(testFilesAmount);
        ArrayList<DeleteFile> deleteFiles = new ArrayList<>(testFilesAmount);
        byte[] bytes = new byte[128];

        for (int i = 0; i < testFilesAmount; i++) {
            String generated = "/" + insecure.nextAlphabetic(96) + ".sh";
            writeFiles.add(new WriteFile(generated, bytes));
            deleteFiles.add(new DeleteFile(generated));
        }

        while (true) {
            for (WriteFile writeFile : writeFiles) {
                jacobFS.executeCommand(writeFile);
            }
            System.out.println("writes loop done");
            for (DeleteFile deleteFile : deleteFiles) {
                jacobFS.executeCommand(deleteFile);
            }
            System.out.println("deletes loop done");
        }
    }

    @Test
    void sameDirPerformanceTest() throws IOException {
        WriteFile.addFileTime = 0L;
        WriteFile.makeDirTimes = 0L;
        WriteFile.persistTreeTimes = 0L;
        int testFilesAmount = 8192;
        byte[] bytes = new byte[1024];
        String sameDir = "/";
        System.out.println("path length = " + (sameDir.length() + 9));

        ArrayList<WriteFile> generated = new ArrayList<>(testFilesAmount);
        for (int i = 0; i < testFilesAmount; i++) {
            generated.add(new WriteFile(sameDir + insecure.nextAlphabetic(96) + ".sh", bytes));
        }
        int counter = 0;
        long timer = System.nanoTime();
        try {
            long lastTime = 0L;
            for (WriteFile wf : generated) {
                lastTime = System.nanoTime();
                jacobFS.executeCommand(wf);
                lastTime = System.nanoTime() - lastTime;
                counter++;
            }
            int avgCounter = counter - 10;
            System.out.println("counter " + counter);
            System.out.println("time " + (System.nanoTime() - timer) / 1000000 + "ms");
            System.out.println("last time " + lastTime + "ns");
            System.out.println("total container size: " + new File(TEST_CNT_PATH).length());
            System.out.println("avg make dir time: " + (WriteFile.makeDirTimes / avgCounter) + "ns");
            System.out.println("avg add file time: " + (WriteFile.addFileTime / avgCounter) + "ns");
            System.out.println("avg persist tree time: " + (WriteFile.persistTreeTimes / avgCounter) + "ns");
            System.out.println("last make dir time: " + WriteFile.lastMakeDirTime + "ns");
            System.out.println("last add file: " + WriteFile.lastAddFileTime + "ns");
            System.out.println("last persist tree: " + WriteFile.lastPersistTreeTime + "ns");
        } catch (Exception e) {
//            stopWatch.stop();
//            System.out.println("counter " + counter);
//            System.out.println("time " + stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms");
//            System.out.println("total container size: " + new File(TEST_CNT_PATH).length());
//            System.out.println("make dir times: " + (WriteFile.makeDirTimes/counter) + "ns");
//            System.out.println("save file times: " + (WriteFile.saveDirTimes/counter) + "ns");
            e.printStackTrace();
        }
        reopenTestContainer();
        List<String> strings = jacobFS.executeCommand(new ListAllFiles());
        System.out.println("strings size = " + strings.size());
    }

    @Test
    void uniquePathsPerformanceTest() throws IOException {
        WriteFile.addFileTime = 0L;
        WriteFile.makeDirTimes = 0L;
        WriteFile.persistTreeTimes = 0L;
        byte[] bytes = new byte[1024];
        String pathLength = generatePath(10, 9);
        System.out.println("path length: " + pathLength.length());
        int total = 8192;
        ArrayList<WriteFile> generated = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            generated.add(new WriteFile(generatePath(10, 9), bytes));
        }
        int counter = 0;
        try {
            long timer = System.nanoTime();
            long lastTime = 0L;
            for (WriteFile wf : generated) {
                lastTime = System.nanoTime();
                jacobFS.executeCommand(wf);
                lastTime = System.nanoTime() - lastTime;
                counter++;
            }

            int avgCounter = counter - 10;
            System.out.println("counter " + counter);
            System.out.println("time " + (System.nanoTime() - timer) / 1000000 + "ms");
            System.out.println("last time " + lastTime + "ns");
            System.out.println("total container size: " + new File(TEST_CNT_PATH).length());
            System.out.println("avg make dir time: " + (WriteFile.makeDirTimes / avgCounter) + "ns");
            System.out.println("avg add file time: " + (WriteFile.addFileTime / avgCounter) + "ns");
            System.out.println("avg persist tree time: " + (WriteFile.persistTreeTimes / avgCounter) + "ns");
            System.out.println("last make dir time: " + WriteFile.lastMakeDirTime + "ns");
            System.out.println("last add file: " + WriteFile.lastAddFileTime + "ns");
            System.out.println("last persist tree: " + WriteFile.lastPersistTreeTime + "ns");
        } catch (Exception e) {
//            stopWatch.stop();
//            System.out.println("counter " + counter);
//            System.out.println("time " + stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms");
//            System.out.println("total container size: " + new File(TEST_CNT_PATH).length());
//            System.out.println("make dir times: " + (WriteFile.makeDirTimes/counter) + "ns");
//            System.out.println("save file times: " + (WriteFile.saveDirTimes/counter) + "ns");
            e.printStackTrace();
        }
        reopenTestContainer();
        List<String> strings = jacobFS.executeCommand(new ListAllFiles());
        System.out.println("strings size = " + strings.size());
    }

//    @Test
//    void uniquePathsReadsPerformanceTest() {
//        byte[] bytes = new byte[1024*100];
//        int files = 8000;
//        String pathLength = generatePath(10, 9);
//        System.out.println("path length: " + pathLength.length());
//        ArrayList<String> paths = new ArrayList<>(files);
//        for (int i = 0; i < files; i++) {
//            paths.add(generatePath(10, 9));
//        }
//        ArrayList<WriteFile> generatedWrites = new ArrayList<>(files);
//        for (String path : paths) {
//            generatedWrites.add(new WriteFile(path, bytes));
//        }
//
//        int counter = 0;
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        try {
//            for (WriteFile wf : generatedWrites) {
//                jacobFS.executeCommand(wf);
//                counter++;
//            }
//        } catch (Exception e) {
//            stopWatch.stop();
//            System.out.println("WRITES");
//            System.out.println("counter " + counter);
//            System.out.println("time " + stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms");
//            System.out.println("total container size: " + new File(TEST_CNT_PATH).length());
//        }
//
//        ArrayList<ReadFileBytes> generatedReads = new ArrayList<>(files);
//        for (String path : paths) {
//            generatedReads.add(new ReadFileBytes(path));
//        }
//        counter = 0;
//        stopWatch = new StopWatch();
//        stopWatch.start();
//        try {
//            for (ReadFileBytes wf : generatedReads) {
//                jacobFS.executeCommand(wf);
//                counter++;
//            }
//        } catch (Exception e) {
//            stopWatch.stop();
//            System.out.println("READS");
//            System.out.println("counter " + counter);
//            System.out.println("time " + stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms");
//            System.out.println("total container size: " + new File(TEST_CNT_PATH).length());
//        }
//    }


    private String generatePath(int depth, int dirLength) {
        return "/" + IntStream.range(0, depth - 1).mapToObj(i -> insecure.nextAlphabetic(dirLength)).collect(Collectors.joining("/"))
                + "/" + insecure.nextAlphabetic(depth - 4) + ".sh";
    }
}