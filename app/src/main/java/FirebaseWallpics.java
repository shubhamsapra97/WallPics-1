import android.app.Application;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Rachit on 2/24/2017.
 */
public class FirebaseWallpics extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(!com.google.firebase.FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
