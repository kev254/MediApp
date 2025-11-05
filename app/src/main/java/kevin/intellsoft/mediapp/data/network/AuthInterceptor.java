package kevin.intellsoft.mediapp.data.network;

import android.content.Context;
import java.io.IOException;
import kevin.intellsoft.mediapp.util.AuthManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = AuthManager.getToken(context);

        if (token != null && !token.isEmpty()) {
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + token);
            return chain.proceed(builder.build());
        }
        return chain.proceed(original);
    }
}
