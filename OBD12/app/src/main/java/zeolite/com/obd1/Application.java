package zeolite.com.obd1;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;

/**
 * Created by Zeolite on 16/2/29.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "D7n0sd0CxYL5qTnbAyMTgIFC-gzGzoHsz", "xtT01rcdGUg2SUn7R3MeBTLP");

//        AVObject testObject = new AVObject("TestObject");
//        testObject.put("pid", "50");
//        testObject.put("222", "111");
//        testObject.saveInBackground();


//        AVOSCloud.initialize(this, "D7n0sd0CxYL5qTnbAyMTgIFC-gzGzoHsz",
//                "xtT01rcdGUg2SUn7R3MeBTLP");
        // 启用崩溃错误统计
        AVAnalytics.enableCrashReport(this.getApplicationContext(), true);
        AVOSCloud.setLastModifyEnabled(true);
        AVOSCloud.setDebugLogEnabled(true);

    }
}
