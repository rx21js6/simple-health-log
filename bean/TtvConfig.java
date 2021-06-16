package jp.nauplius.app.ttv1.common.ui.bean;

import lombok.Data;

/**
 * JTalk設定情報
 */
@Data
public class TtvConfig {
    /**
     * Key
     */
    private String iv;
    /**
     * IV
     */
    private String key;
    /**
     * ホームディレクトリ
     */
    private String jtalkHome;
    /**
     * 実行ファイルパス
     */
    private String jtalkBin;
    /**
     * ディクショナリディレクトリ
     */
    private String dicDir;
    /**
     * 認証設定
     */
    private boolean ttvAuth;
    /**
     * 認証先URL
     */
    private String ttvAuthUrl;
    /**
     * 強制登録可否
     */
    private boolean enableForceRegistration;
}
