package com.example.admin.frameworkdemo.communication;

import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;

import com.example.admin.frameworkdemo.MyApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Connection;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Http Communication.
 * <p>
 *
 * @author jacklee
 */

public class HttpCommunication extends Communication {

    private static final String PREFIX = HttpCommunication.class.getSimpleName();

    private Disposable connector;
    private Connection connection;

    private ByteArrayOutputStream receiveBuf = new ByteArrayOutputStream();

    // Service Url.
    private String serviceUrl;

    public HttpCommunication(String serviceUrl, boolean sslEnabled,
                             int connectTimeout, int receiveTimeout) {
        this.serviceUrl = serviceUrl;
        this.sslEnabled = sslEnabled;
        this.connectTimeout = connectTimeout;
        this.receiveTimeout = receiveTimeout;
    }

    public HttpCommunication(String serviceUrl, boolean sslEnabled) {
        this.serviceUrl = serviceUrl;
        this.sslEnabled = sslEnabled;
        this.connectTimeout = connectTimeout;
        this.receiveTimeout = receiveTimeout;
    }

    /**
     * Send and receive.
     *
     * @param sendingData The sending data.
     * @return ObservableSource which emits CommunicationEvent
     */
    @Override
    public Observable<CommunicationEvent> sendAndReceive(final byte[] sendingData) {

        String errorMessage = "Fail to receive data";

        return Observable.create(
                emitter -> {
                    RequestBody body = RequestBody.create(MediaType.parse("text/plain"), sendingData);

                    connector = getRetrofit()
                            .create(PostService.class)
                            .transaction(body)
                            .doOnSubscribe(disposable -> emitter.onNext(new CommunicationEvent(CommunicationEvent.CONNECTING)))
                            .doOnDispose(() -> {

                                if (connection != null) {
                                    if (connection.socket().isConnected()) {
                                        emitter.onNext(new CommunicationEvent(CommunicationEvent.CONNECTED));
                                        emitter.onNext(new CommunicationEvent(CommunicationEvent.SENDING));
                                        emitter.onNext(new CommunicationEvent(CommunicationEvent.SENDED, sendingData));
                                        emitter.onNext(new CommunicationEvent(CommunicationEvent.RECEIVEING));

                                        emitter.onError(new CommunicationException(FAIL_TO_RECEIVE_DATA, new Throwable(), CommunicationException.RECEIVE_FAILED));
                                    } else {
                                        emitter.onError(new CommunicationException(FAIL_TO_CONNECT, new Throwable(), CommunicationException.CONNECT_FAILED));
                                    }
                                } else {
                                    emitter.onError(new CommunicationException(FAIL_TO_CONNECT, new Throwable(), CommunicationException.CONNECT_FAILED));
                                }
                            })
                            .subscribe(responseBody -> receiveBuf.write(responseBody.bytes()),
                                    throwable -> {
                                        if (throwable instanceof ConnectException) {
                                            emitter.onError(new CommunicationException(FAIL_TO_CONNECT, throwable, CommunicationException.CONNECT_FAILED));
                                        } else {
                                            emitter.onNext(new CommunicationEvent(CommunicationEvent.CONNECTED));
                                            emitter.onNext(new CommunicationEvent(CommunicationEvent.SENDING));
                                            emitter.onNext(new CommunicationEvent(CommunicationEvent.SENDED, sendingData));
                                            emitter.onNext(new CommunicationEvent(CommunicationEvent.RECEIVEING));
                                            emitter.onError(new CommunicationException(FAIL_TO_RECEIVE_DATA, throwable, CommunicationException.RECEIVE_FAILED));
                                        }
                                    },
                                    () -> {
                                        emitter.onNext(new CommunicationEvent(CommunicationEvent.CONNECTED));
                                        emitter.onNext(new CommunicationEvent(CommunicationEvent.SENDING));
                                        emitter.onNext(new CommunicationEvent(CommunicationEvent.SENDED, sendingData));
                                        emitter.onNext(new CommunicationEvent(CommunicationEvent.RECEIVEING));
                                        emitter.onNext(new CommunicationEvent(CommunicationEvent.RECEIVED, receiveBuf.toByteArray()));

                                        emitter.onComplete();
                                    });
                });
    }

    /**
     * Get retrofit.
     *
     * @return Retrofit instance
     */
    private Retrofit getRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(receiveTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false);

        if (sslEnabled) {
            SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
            builder = builder.sslSocketFactory(sslSocketFactory, Platform.get().trustManager(sslSocketFactory))
                    .hostnameVerifier((s, sslSession) -> true);
        }

        OkHttpClient okHttpClient = builder.addNetworkInterceptor(chain -> {

            connection = chain.connection();

            Request request = chain.request();

            Response response = chain.proceed(request);

            return response;
        }).build();


        return new Retrofit.Builder()
                .baseUrl(serviceUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
                .client(okHttpClient)
                .build();

    }

    /**
     * Get SSL Socket Factory
     *
     * @return SSLSocketFactory instance
     */
    private SSLSocketFactory getSSLSocketFactory() {

        try {
            AssetManager assetManager = MyApplication.getContext().getAssets();
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            SSLContext sslContext = SSLContext.getInstance(protocol);
            Log.i(PREFIX, "--->使用的证书：" + certificateFile);
            if (mutualAuthEnabled) {
                InputStream clientCertIn = assetManager.open(certificateFile);
                InputStream clientPrivateKeyIn = assetManager.open(privateKeyFile);
                InputStream caFileIn = assetManager.open(caFile);

                Certificate certificate = certificateFactory.generateCertificate(caFileIn);
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                trustStore.setCertificateEntry("ca", certificate);

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);

                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                final X509TrustManager origTrustmanager = (X509TrustManager) trustManagers[0];

                TrustManager[] wrappedTrustManagers = new TrustManager[]{
                        new X509TrustManager() {

                            @Override
                            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                                origTrustmanager.checkClientTrusted(x509Certificates, s);
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return origTrustmanager.getAcceptedIssuers();
                            }
                        }
                };
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                        KeyManagerFactory.getDefaultAlgorithm());
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);

                Certificate clientCert = certificateFactory.generateCertificate(clientCertIn);

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();

                byte[] data = new byte[4096];
                int count = -1;
                while ((count = clientPrivateKeyIn.read(data, 0, 4096)) != -1){
                    outStream.write(data, 0, count);
                }

                String key = new String(outStream.toByteArray(), "ISO-8859-1");

                StringBuilder pkcs8Lines = new StringBuilder();
                BufferedReader rdr = new BufferedReader(new StringReader(key));
                String line;
                while ((line = rdr.readLine()) != null) {
                    pkcs8Lines.append(line);
                }

                String pkcs8Pem = pkcs8Lines.toString();
                pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
                pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
                pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

                byte[] encoderByte = Base64.decode(pkcs8Pem, Base64.DEFAULT);

                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoderByte);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = kf.generatePrivate(keySpec);
                keyStore.setKeyEntry("client", privateKey, null, new Certificate[]{clientCert});

                keyManagerFactory.init(keyStore, null);

                sslContext.init(keyManagerFactory.getKeyManagers(), wrappedTrustManagers, null);

                clientCertIn.close();
                clientPrivateKeyIn.close();
                caFileIn.close();

                return sslContext.getSocketFactory();
            } else {
                InputStream inputStream = assetManager.open(certificateFile);
                Certificate certificate = certificateFactory.generateCertificate(inputStream);

                KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
                keystore.load(null, certificatePassword.toCharArray());
                keystore.setCertificateEntry("ca", certificate);

                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
                trustManagerFactory.init(keystore);

                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

                inputStream.close();

                return sslContext.getSocketFactory();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Disconnect.
     */
    @Override
    public void disconnect() {
        if (connector != null && !connector.isDisposed()) {
            connector.dispose();
        }
    }

    @Override
    public Observable<CommunicationEvent> connect() {
        return Observable.error(new Throwable(OPERATION_NOT_SUPPORT));
    }

    @Override
    public Observable<CommunicationEvent> send(final byte[] sendingData) {
        return Observable.error(new Throwable(OPERATION_NOT_SUPPORT));
    }

    @Override
    public Observable<CommunicationEvent> receive() {
        return Observable.error(new Throwable(OPERATION_NOT_SUPPORT));
    }

    @Override
    public Observable<CommunicationEvent> receive(int length) {
        return Observable.error(new Throwable(OPERATION_NOT_SUPPORT));
    }

    /**
     * Interface for retrofit request.
     */
    private interface PostService {
        /**
         * send RequestBody and receive ResponseBody
         *
         * @param body request data
         * @return ObservableSource which emits ResponseBody
         */
        @POST("/")
        Observable<ResponseBody> transaction(@Body RequestBody body);
    }
}
