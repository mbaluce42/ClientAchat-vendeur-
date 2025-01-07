package ServeurGeneriqueTCP.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigServeurManager
{
    private static final String CONFIG_FILE = "configServeur.properties";

    private static final Properties props = new Properties();

    public ConfigServeurManager()
    {
        readConfig();
    }

    public static void readConfig()
    {
        File configFile = new File(CONFIG_FILE);
        System.out.println("Chemin absolu du fichier : " + configFile.getAbsolutePath());
        System.out.println("Le fichier existe : " + configFile.exists());

        try {
            if (!configFile.exists()) {
                System.out.println("Création du fichier de configuration...");
                props.setProperty("PORT", "50001");
                props.setProperty("TAILLE_POOL", "2");
                props.setProperty("MODE_POOL", "true");

                props.store(new FileOutputStream(configFile), null);
            }
            else {
                System.out.println("Chargement du fichier de configuration existant...");
                props.load(new FileInputStream(configFile));
                // Afficher les propriétés chargées
                System.out.println("Propriétés chargées : " + props);
            }
        } catch (IOException e) {
            System.out.println("Erreur configuration: " + e.getMessage());
            System.exit(1);
        }
    }

    public int getPort()
    {
        return Integer.parseInt(props.getProperty("PORT"));
    }

    public int getTaillePool()
    {
        return Integer.parseInt(props.getProperty("TAILLE_POOL"));
    }

    public boolean getModePool()
    {
        return Boolean.parseBoolean(props.getProperty("MODE_POOL"));
    }
}