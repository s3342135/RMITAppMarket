package vn.edu.rmit.Tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

/**
 * This class handle network connection. Methods and functions are provided to
 * send and receive app info, to signin and signup.
 * 
 * @author Vuong Do Thanh Huy
 * 
 */
public class NetworkConnection {

	private static final String URL_SIGNIN = "https://mekong.rmit.edu.vn/~s3342135/android/login.php?";
	private static final String URL_SIGNUP = "https://mekong.rmit.edu.vn/~s3342135/android/signup.php?";
	private static final String URL_RECEIVEAPP = "https://mekong.rmit.edu.vn/~s3342135/android/loadapps.php";
	private static final String URL_SENDAPP = "https://mekong.rmit.edu.vn/~s3342135/android/submitapps.php?";
	private static final String MEKONG_URL = "https://mekong.rmit.edu.vn/~s3342135/android";
	private static String appList[] = null;

	public String[] getAppList() {
		return appList;
	}

	public void setAppList(String appData) {
		// process string array
		android.util.Log.i("setAppList", "Set appList run");
		appList = appData.split("##");
	}

	public boolean signIn(final String idInput, final String passwdInput) {
		HttpsURLConnection hc = null;
		URL url = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		boolean finishSignIn = false;

		try {
			trustEveryone(); // to prevent SSL Cert exception
			String id = "id=" + idInput;
			String passwd = "&passwd=" + passwdInput;
			url = new URL(URL_SIGNIN + id + passwd);
			hc = (HttpsURLConnection) url.openConnection();
			hc.setDoInput(true);
			hc.setDoOutput(true);
			hc.setUseCaches(false);
			hc.connect();

			is = hc.getInputStream();
			
			baos = new ByteArrayOutputStream();

			byte[] buff = new byte[256];
			final int EOL = -1;
			while (true) {
				int rd = is.read(buff, 0, 256);
				if (rd == EOL) {
					break;
				}
				baos.write(buff, 0, rd);
			}
			baos.flush();
			buff = baos.toByteArray();

			String result = new String(buff);
			
			if (result.equalsIgnoreCase("0")) {
				// login OK
				finishSignIn = true;
			} else {
				// login failed
				finishSignIn = false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (hc != null) {
				hc.disconnect();
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		}
		return finishSignIn;
	}

	public boolean signUp(final String fullnameInput, final String idInput,
			final String passwdInput) {
		HttpsURLConnection hc = null;
		URL url = null;
		DataOutputStream dos = null;
		InputStream dis = null;
		ByteArrayOutputStream baos = null;
		boolean finishSignUp = false;

		try {
			trustEveryone(); // to prevent SSL Cert exception
			String fullname = "fullname=" + fullnameInput;
			String id = "&id=" + idInput;
			String passwd = "&passwd=" + passwdInput;
			url = new URL(URL_SIGNUP + fullname + id + passwd);
			hc = (HttpsURLConnection) url.openConnection();
			hc.setDoInput(true);
			hc.setDoOutput(true);
			hc.setUseCaches(false);
			hc.connect();
			dis = hc.getInputStream();
			dos = (DataOutputStream) hc.getOutputStream();
			baos = new ByteArrayOutputStream();

			byte[] buff = new byte[256];
			final int EOL = -1;
			while (true) {
				int rd = dis.read(buff, 0, 256);
				if (rd == EOL) {
					break;
				}
				baos.write(buff, 0, rd);
			}
			baos.flush();
			buff = baos.toByteArray();

			String result = new String(buff);

			if (result.equalsIgnoreCase("0")) {
				// sign up OK
				finishSignUp = true;
			} else {
				// sign up failed: ID exist!
				finishSignUp = false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (hc != null) {
				hc.disconnect();
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
				}
			}
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		}
		return finishSignUp;
	}

	public boolean submitApp(final String appNameInput,
			final String appDescInput, final File apkInput) {
		HttpsURLConnection hc = null;
		URL url = null;
		DataOutputStream dos = null;
		InputStream dis = null;
		ByteArrayOutputStream baos = null;
		boolean finishSubmit = false;

		// http://stackoverflow.com/questions/3204476/android-file-uploader-with-server-side-php
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		FileInputStream fileInputStream = null;
		//

		try {
			trustEveryone(); // to prevent SSL Cert exception
			String appName = "appname=" + appNameInput;
			String appDesc = "&desc=" + appDescInput;
			url = new URL(URL_SENDAPP + appName + appDesc);
			hc = (HttpsURLConnection) url.openConnection();

			// http://stackoverflow.com/questions/3204476/android-file-uploader-with-server-side-php
			hc.setRequestMethod("POST");
			hc.setRequestProperty("Connection", "Keep-Alive");
			hc.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			//

			hc.setDoInput(true);
			hc.setDoOutput(true);
			hc.setUseCaches(false);
			hc.connect();
			dis = hc.getInputStream();
			dos = (DataOutputStream) hc.getOutputStream();
			baos = new ByteArrayOutputStream();

			// http://stackoverflow.com/questions/3204476/android-file-uploader-with-server-side-php
			fileInputStream = new FileInputStream(apkInput);
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: post-data; name=uploadedfile;filename="
					+ apkInput.getPath() + "" + lineEnd);
			dos.writeBytes(lineEnd);

			// create a buffer of maximum size
			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1000;
			// int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bytesAvailable];

			// read file and write it into form...
			int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bytesAvailable);
				bytesAvailable = fileInputStream.available();
				bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
			}

			// send multipart form data necessary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			//

			byte[] buff = new byte[256];
			final int EOL = -1;
			while (true) {
				int rd = dis.read(buff, 0, 256);
				if (rd == EOL) {
					break;
				}
				baos.write(buff, 0, rd);
			}
			baos.flush();
			buff = baos.toByteArray();

			String result = new String(buff);

			if (result.equalsIgnoreCase("0")) {
				// applications submit OK
				finishSubmit = true;
			} else {
				// applications submit failed: something went wrong!
				finishSubmit = false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (hc != null) {
				hc.disconnect();
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
				}
			}
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return finishSubmit;
	}

	public boolean downloadAppList() {

		android.util.Log.i("Android download application list", "Download");
		HttpsURLConnection hc = null;
		URL url = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		boolean finishDownloadAppList = false;

		try {
			trustEveryone(); // to prevent SSL Cert exception
			url = new URL(URL_RECEIVEAPP);
			hc = (HttpsURLConnection) url.openConnection();
			hc.setDoInput(true);
			hc.setDoOutput(true);
			hc.setUseCaches(false);
			hc.connect();
			is = hc.getInputStream();
			baos = new ByteArrayOutputStream();

			byte[] buff = new byte[256];
			final int EOL = -1;
			while (true) {
				int rd = is.read(buff, 0, 256);
				if (rd == EOL) {
					break;
				}
				baos.write(buff, 0, rd);
			}
			baos.flush();
			buff = baos.toByteArray();

			String result = new String(buff);

			// append data to var. May disable if use db instead.
			setAppList(result);

			if (result.equalsIgnoreCase("0")) {
				// data received OK
				finishDownloadAppList = true;
			} else {
				// failed: something went wrong!
				finishDownloadAppList = false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (hc != null) {
				hc.disconnect();
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		}
		return finishDownloadAppList;
	}

	public boolean downloadApp(final String appURL, final String whereToSave,
			final String fileName) {
		URL url = null;
		HttpsURLConnection hc = null;
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			trustEveryone();
			url = new URL(MEKONG_URL + appURL);
			hc = (HttpsURLConnection) url.openConnection();
			hc.setRequestMethod("GET");
			hc.setDoOutput(true);
			hc.connect();

			File dir = new File(whereToSave);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File f = new File(dir, fileName);
			fos = new FileOutputStream(f);

			is = hc.getInputStream();

			byte[] buffer = new byte[1024];
			int len = 0;
			final int EOF = -1;
			while ((len = is.read(buffer)) != EOF) {
				fos.write(buffer, 0, len);
			}
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (hc != null) {
				hc.disconnect();
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean downloadImage(final String imageURL,
			final String whereToSave, final String fileName) {
		URL url = null;
		HttpsURLConnection hc = null;
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			trustEveryone();
			url = new URL(MEKONG_URL + imageURL);
			hc = (HttpsURLConnection) url.openConnection();
			hc.setRequestMethod("GET");
			hc.setDoOutput(true);
			hc.connect();

			File dir = new File(whereToSave);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File f = new File(dir, fileName);
			fos = new FileOutputStream(f);

			is = fetch(MEKONG_URL + imageURL);

			Bitmap bm = BitmapFactory.decodeStream(is);
			bm.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			bm = BitmapFactory.decodeFile(f.getAbsolutePath());
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (hc != null) {
				hc.disconnect();
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static InputStream fetch(String address)
			throws MalformedURLException, IOException {
		HttpGet httpRequest = new HttpGet(URI.create(address));
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		InputStream instream = bufHttpEntity.getContent();
		return instream;
	}

	/*
	 * http://stackoverflow.com/questions/2642777/trusting-all-certificates-using
	 * -httpclient-over-https
	 */
	private void trustEveryone() {
		try {
			HttpsURLConnection
					.setDefaultHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String hostname,
								SSLSession session) {
							return true;
						}
					});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context
					.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}
}