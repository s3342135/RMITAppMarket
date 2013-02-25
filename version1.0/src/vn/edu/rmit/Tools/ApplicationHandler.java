package vn.edu.rmit.Tools;

import android.net.Uri;

public class ApplicationHandler {

	private static ApplicationHandler applicationHandler = new ApplicationHandler();
	private String[] applicationNames;
	private String[] applicationDescription;
	private float[] ratingStar;
	private Uri[] applicationImageUris;
	private String[] applicationImageNames;
	private String[] installPath;

	private ApplicationHandler() {
	}

	public static ApplicationHandler getApplicationHandler() {
		return applicationHandler;
	}

	public String[] getApplicationNames() {
		return applicationNames;
	}

	public void setApplicationNames(String[] applicationNames) {
		this.applicationNames = applicationNames;
	}

	public Uri[] getApplicationImageUris() {
		return applicationImageUris;
	}

	public String[] getApplicationDescription() {
		return applicationDescription;
	}

	public void setApplicationDescription(String[] applicationDescription) {
		this.applicationDescription = applicationDescription;
	}

	public float[] getRatingStar() {
		return ratingStar;
	}
	
	public void setRatingStar(float[] ratingStar) {
		this.ratingStar = ratingStar;
	}
	
	public void setApplicationImageUris(Uri[] uri) {
		this.applicationImageUris = uri;
	}

	public Uri getImageUriAtPosition(int position) {
		return applicationImageUris[position];
	}

	public String[] getApplicationImageNames() {
		return applicationImageNames;
	}

	public void setImageNames(String[] imageNames) {
		this.applicationImageNames = imageNames;
	}

	public String getImageNameAtPosition(int position) {
		return applicationImageNames[position];
	}
	
	public String[] getInstallPath() {
		return installPath;
	}
	
	public void setInstallPath(String[] installPath) {
		this.installPath = installPath;
	}
}
