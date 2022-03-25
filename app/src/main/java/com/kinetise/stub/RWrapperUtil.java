package com.kinetise.stub;
import com.kinetise.helpers.IDrawableHolder;
import com.kinetise.helpers.IStringHolder;
import com.kinetise.helpers.RWrapper;

public class RWrapperUtil {

    private static void injectDrawable(){
        drawable d = new RWrapperUtil.drawable();
        RWrapper.drawable.setDrawableHolder(d);
    }

    private static void injectString(){
        string sString = new RWrapperUtil.string();
        RWrapper.string.setStringHolder(sString);
    }

    public static void inject(){
        injectDrawable();
        injectString();
    }

    private static class drawable implements IDrawableHolder {
        @Override
        public int getIcon(){
            return com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.drawable.icon;
        }
    }

    private static class string implements IStringHolder{

        @Override
        public int getErrorPlaceholder(){
            return image_placeholderError;
        }

        public static final int image_placeholderError = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.string.image_placeholderError;
        @Override
        public int getLoadingPlaceholder(){
            return image_placeholderLoading;
        }
        public static final int image_placeholderLoading = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.string.image_placeholderLoading;
        @Override
        public int getScannedQRCodePlaceholder(){
            return image_placeholderQR;
        }
        public static final int image_placeholderQR = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.string.image_placeholderQR;

        public static final int push_google_project_number = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.string.push_google_project_number;

        public static final int push_registration_url = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.string.push_registration_url;
        @Override
        public int getCurtainPlaceholder(){
            return image_placeholderCurtain;
        }
        public static final int image_placeholderCurtain = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.string.curtain_image;
        @Override
        public int getMapKey(){
            return map_key;
        }
        public static final int map_key = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.string.map_key;
        @Override
        public int getDeveloperAssetsPrefix(){
            return developer_assets_prefix;
        }
        public static final int developer_assets_prefix = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.string.developer_assets_prefix;

        public static final int image_placeholderMap = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.string.image_placeholderMap;
        @Override
        public int getMapPlaceholder() {
            return image_placeholderMap;
        }

        public static final int use_crashlytics = com.kinetise.app7a7d2e24e1a13afc6dff6a9b6a4d5d14.R.bool.use_crashlytics;
    }

}
