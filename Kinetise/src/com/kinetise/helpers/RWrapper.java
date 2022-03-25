package com.kinetise.helpers;

import com.kinetise.R;

public class RWrapper {

    public static final class attr {
    }

    public static class drawable {

        private static IDrawableHolder sHolder;

        public static void setDrawableHolder(IDrawableHolder holder) {
            sHolder = holder;
        }

        public static int getIcon() {
            if (sHolder != null) {
                return sHolder.getIcon();
            }
            return R.drawable.icon;
        }

        public static final int notification_icon_since_android_l = R.drawable.notification_android_since_android_l;

        public static final int animation20 = R.drawable.loading_20;
        public static final int animation30 = R.drawable.loading_30;
        public static final int animation40 = R.drawable.loading_40;
        public static final int animation60 = R.drawable.loading_60;
        public static final int animation100 = R.drawable.loading_100;
        public static final int animation150 = R.drawable.loading_150;
        public static final int animation200 = R.drawable.loading_200;
    }

    public static final class id {
        public static final int debugTouchInterceptView = R.id.touchInterceptView;
        public static final int rootLayout = R.id.rootLayout;
        public static final int mainDisplay = R.id.mainDisplay;
        public static final int webView = R.id.webView;
        public static final int progressBar = R.id.progressBar;
        public static final int infoHolder = R.id.infoHolder;
        public static final int errorMessage = R.id.errorMessage;
        public static final int retryButton = R.id.retryButton;
        public static final int fullscreen_video_view = R.id.fullscreen_video_view;
        public static final int main = R.id.mainLayout;
        public static final int video_view = R.id.video_view;
        public static final int open_fullscreen_video_button = R.id.fullscreen_button;
        public static final int exit_fullscreen_button = R.id.exit_fullscreen_button;
    }

    public static final class layout {
        public static final int activity_main = R.layout.activity_main;
        public static final int mapPlaceholder = R.layout.map_placeholder;
        public static final int fullscreen_webview = R.layout.fullscreen_webview;
        public static final int fullscreen_video_dialog = R.layout.fullscreen_video;
        public static final int video_view = R.layout.video_view;
    }

    public static class bool {
        public static int app_state_saving = R.bool.app_state_saving;
        public static int use_crashlytics = R.bool.use_crashlytics;
        public static int use_stetho = R.bool.use_stetho;
    }

    public static class string {
        //FIXME RO get rid of the holder that is unneceserry and is increasing the complexity of working with the class
        private static IStringHolder sHolder;

        public static int fb_client_id_key = R.string.fb_client_id_key;
        public static int fb_client_id_value = R.string.fb_client_id_value;
        public static int fb_client_secret_key = R.string.fb_client_secret_key;
        public static int fb_client_secret_value = R.string.fb_client_secret_value;
        public static int fb_auth_url = R.string.fb_auth_url;


        public static int twitter_key = R.string.twitter_key;
        public static int twitter_secret = R.string.twitter_secret;

        public static int linkedin_key = R.string.linkedin_key;
        public static int linkedin_secret = R.string.linkedin_secret;

        public static int compilation_version = R.string.compilation;
        public static int blowjobber_version = R.string.blowjobber_version;
        public static int descriptor_compier_version = R.string.descriptor_compiler_version;
        public static int compilator_version = R.string.compilator_version;

        public static int compilation_date = R.string.compilation_date;
        public static int xml_dat = R.string.xml_date;
        public static int blowjobber_date = R.string.blowjobber_date;
        public static int code_date = R.string.code_date;
        public static int descriptor_compiler_date = R.string.descriptor_compiler_date;

        public static void setStringHolder(IStringHolder pHolder) {
            sHolder = pHolder;
        }

        public static final int app_name = R.string.app_name;

        public static final int push_project_id = R.string.push_project_id;

        public static int getErrorPlaceholder() {
            if (sHolder != null) {
                return sHolder.getErrorPlaceholder();
            }
            return image_placeholderError;
        }

        public static final int image_placeholderError = R.string.image_placeholderError;

        public static int getLoadingPlaceholder() {
            if (sHolder != null) {
                return sHolder.getLoadingPlaceholder();
            }
            return image_placeholderLoading;
        }

        public static final int image_placeholderLoading = R.string.image_placeholderLoading;

        public static int getScannedQRCodePlaceholder() {
            if (sHolder != null) {
                return sHolder.getScannedQRCodePlaceholder();
            }
            return image_placeholderQR;
        }

        public static final int image_placeholderQR = R.string.image_placeholderQR;

        public static final int push_google_project_number = R.string.push_google_project_number;

        public static final int google_sign_in_client_id = R.string.google_sign_in_client_id;

        public static final int push_registration_url = R.string.push_registration_url;

        public static int getCurtainPlaceholder() {
            if (sHolder != null) {
                return sHolder.getCurtainPlaceholder();
            }
            return image_placeholderCurtain;
        }

        public static final int image_placeholderCurtain = R.string.curtain_image;

        public static int getMapKey() {
            if (sHolder != null) {
                return sHolder.getMapKey();
            }
            return map_key;
        }

        public static final int map_key = R.string.map_key;

        public static int getDeveloperAssetsPrefix() {
            if (sHolder != null) {
                return sHolder.getDeveloperAssetsPrefix();
            }
            return developer_assets_prefix;
        }

        public static final int developer_assets_prefix = R.string.developer_assets_prefix;
    }

}
