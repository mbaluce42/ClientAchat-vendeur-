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
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        try
        {
            if (!configFile.exists())
            {
                //cree fichier de config avec valeurs par defaut
                props.setProperty("PORT", "50001");
                props.setProperty("TAILLE_POOL", "2");
                props.setProperty("MODE_POOL", "true");//true = pool, false = a la demande

                props.store(new FileOutputStream(configFile), null);
            }
            else
            {
                //charge les valeurs du fichier de config
                props.load(new FileInputStream(configFile));
            }
        } catch (IOException e) {
            System.out.println("Erreur configuration: " + e.getMessage());
            System.exit(1);
        }
    }

    public int getPort()
    {
        return Integer.parseInt(props.getProperty("PORT", "50001"));
    }

    public int getTaillePool()
    {
        return Integer.parseInt(props.getProperty("TAILLE_POOL", "2"));
    }

    public boolean getModePool()
    {
        return Boolean.parseBoolean(props.getProperty("MODE_POOL", "true"));
    }
}