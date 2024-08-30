package in.innovateria.ultimate_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.zxing.Result;

public interface DecodeCallback {
    @WorkerThread
    void onDecoded(@NonNull Result result);
}

