package us.mifeng.jetpacket1.model;

public class Destination {

    /**
     * isFisFragmentDestination : true
     * asStarter : false
     * needLogin : false
     * pageUrl :  main/tabs/dash
     * claszName : us.mifeng.jetpacket1.ui.home.NotificationsFragment
     */

    private boolean isFisFragmentDestination;
    private boolean asStarter;
    private boolean needLogin;
    private String pageUrl;
    private String claszName;

    public boolean isIsFisFragmentDestination() {
        return isFisFragmentDestination;
    }

    public void setIsFisFragmentDestination(boolean isFisFragmentDestination) {
        this.isFisFragmentDestination = isFisFragmentDestination;
    }

    public boolean isAsStarter() {
        return asStarter;
    }

    public void setAsStarter(boolean asStarter) {
        this.asStarter = asStarter;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getClaszName() {
        return claszName;
    }

    public void setClaszName(String claszName) {
        this.claszName = claszName;
    }
}
