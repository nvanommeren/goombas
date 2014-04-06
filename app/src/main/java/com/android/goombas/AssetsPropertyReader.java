/***********************************************************************
 *
 * Shoot the Goomba's
 * Nikki van Ommeren
 * nikki_vanommeren@hotmail.com, 6229670
 *
 * Helper class for reading the config.properties file in the assets directory.
 * Source: http://khurramitdeveloper.blogspot.nl/2013/07/properties-file-in-android.html
 *
 ***********************************************************************/

package com.android.goombas;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AssetsPropertyReader {

    private Context context;
    private Properties properties;

    public AssetsPropertyReader(Context context) {
        this.context = context;

        // Add a new property object
        properties = new Properties();
    }

    /**
     * Returns the properties from the file with the filename from the input.
     */
    public Properties getProperties(String FileName) {

        try {
             // getAssets() Return an AssetManager instance for your
             // application's package. AssetManager Provides access to an
             // application's raw asset files;
            AssetManager assetManager = context.getAssets();

            // Open an asset using ACCESS_STREAMING mode. This
            InputStream inputStream = assetManager.open(FileName);

            // Loads properties from the specified InputStream,
            properties.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;

    }

}