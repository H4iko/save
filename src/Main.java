import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        System.out.println("To save enter 1, to load enter 2");
        path += "/savegames/";
        int result = scanner.nextInt();
        switch (result) {
            case 1:
                saving(path);
                break;
            case 2:
                loading(path);
                break;
        }

    }

    public static void saveGame(String path, GameProgress save) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(save);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void zipFiles(String path, String[] files) {
        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(path))) {
            for (String file : files) {
                try(FileInputStream input =  new FileInputStream(file)) {
                    File save = new File(file);
                    if (save.exists()) {
                        ZipEntry zip = new ZipEntry(save.getName());
                        out.putNextEntry(zip);
                        out.write(input.readAllBytes());
                        out.closeEntry();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<String> openZip(String path, String directory) {
        List<String> files = new ArrayList<String>();
        try(ZipInputStream input = new ZipInputStream(new FileInputStream(path))) {
            ZipEntry entry;
            while((entry=input.getNextEntry())!=null) {
                String fileName = entry.getName();
                files.add(fileName);
                try (FileOutputStream out = new FileOutputStream(directory + fileName)) {
                    for (int c = input.read(); c != -1; c = input.read()) {
                        out.write(c);
                    }
                    out.flush();
                    input.closeEntry();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return files;
    }

    public static GameProgress openProgress(String path) {
        try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(path))) {
            return (GameProgress) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static void saving(String path) {
        GameProgress save1 = new GameProgress(100, 50, 5, 100.5);
        String save1Path = path+"save1.dat";
        saveGame(save1Path, save1);

        GameProgress save2 = new GameProgress(80, 20, 15, 300.3);
        String save2Path = path+"save2.dat";
        saveGame(save2Path, save2);

        GameProgress save3 = new GameProgress(40, 2, 45, 500.6);
        String save3Path = path+"save3.dat";
        saveGame(save3Path, save3);

        String[] savesToZip = new String[]{save1Path, save2Path, save3Path};

        zipFiles(path + "saves.zip", savesToZip);

        for (String pathToDelete : savesToZip) {
            File deleteFile = new File(pathToDelete);
            if (deleteFile.exists()) {
                if (!deleteFile.delete()) {
                    System.out.println("Error");
                }
            }
        }
    }

    private static void loading(String path) {
        List<String> files = openZip(path + "saves.zip", path);
        for (String file : files) {
            GameProgress gameProgress = openProgress(path + "save1.dat");
            System.out.println(gameProgress);
        }
        File zip = new File(path + "saves.zip");
        if (zip.exists()) {
            if (!zip.delete()){
                System.out.println("Error");
            }
        }
    }
}
