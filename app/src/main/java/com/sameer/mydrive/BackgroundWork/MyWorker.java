package com.sameer.mydrive.BackgroundWork;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.sameer.mydrive.Extra.MainActivity;

public class MyWorker extends Worker {

    public static String filepath = null;
    public static String TAG = "MyWorker";
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void FilepathSet(String Filepath){
        filepath = Filepath;

    }

    @NonNull
    @Override
    public Result doWork() {
        //MainActivity.initSync(filepath);
       //MainActivity.checkSync();
        /*if(filepath != null){
            try{
                MainActivity.initSync(filepath);
            }
            catch (Exception E){
                Log.d(TAG, "doWork: " + E.getMessage());
            }

        }

         */
        //Log.d(TAG, "doWork: " + true);
       // return Result.success();
        return null;
    }
}
