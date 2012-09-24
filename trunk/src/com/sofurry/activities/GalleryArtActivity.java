package com.sofurry.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sofurry.AppConstants;
import com.sofurry.R;
import com.sofurry.adapters.SubmissionGalleryAdapter;
import com.sofurry.base.classes.AbstractContentGallery;
import com.sofurry.base.classes.ActivityManager;
import com.sofurry.mobileapi.ApiFactory.ContentType;
import com.sofurry.mobileapi.ApiFactory.ViewSource;
import com.sofurry.model.Submission;
import com.sofurry.storage.ImageStorage;
import com.sofurry.util.ErrorHandler;

public class GalleryArtActivity extends AbstractContentGallery<Submission> {

	/**
	 * Converts Json-submission objects into a list of Submission objects
	 * @param obj
	 * The base JSON object as returned by the fetcher thread
	 * @throws JSONException
	 */
	public static void jsonToResultlist(JSONObject obj, ActivityManager<Submission> man) throws JSONException {
		JSONArray pagecontents = new JSONArray(obj.getString("pagecontents"));
		man.totalPages = Integer.parseInt(obj.getString("totalpages"));
		JSONArray items = new JSONArray(pagecontents.getJSONObject(0).getString("items"));
		for (int i = 0; i < items.length(); i++) {
			Submission s = new Submission();
			s.setType(man.getContentType());
			s.populate(items.getJSONObject(i));

			man.getResultList().add(s);
			//man.getPageIDs().add("" + s.getId());
		}
	}

//	public Request getFetchParameters(int page, String viewSearch) throws Exception {
//		Request req = ApiFactory.createBrowse(man.getViewSource(),viewSearch,ContentType.art,AppConstants.ENTRIESPERPAGE_GALLERY,page);
//		return req;
//	}
	
	/* (non-Javadoc)
	 * @see com.sofurry.AbstractContentGallery#parseResponse(org.json.JSONObject)
	 */
	public void parseResponse(JSONObject obj) {
		try {
			jsonToResultlist(obj, man);
		} catch (Exception e) {
			man.onError(e);
		}
		// Start downloading the thumbnails
		man.startThumbnailDownloader();
	}

	@Override
	public void setSelectedIndex(int selectedIndex) {
		Submission s = getDataItem(selectedIndex);
		Log.i(AppConstants.TAG_STRING, "GalleryArt: Viewing art ID: " + s.getId());
		Intent i = new Intent(this, ViewArtActivity.class);
		s.feedIntent(i);
		// allow viewer to know submissions list
		i.putExtra("list", man.getResultList()); 
		i.putExtra("listId", selectedIndex); 
		startActivity(i);
	}


	@Override
	public BaseAdapter getAdapter(Context context) {
		return new SubmissionGalleryAdapter(context, man.getResultList());
	}

	public void resetViewSourceExtra(ViewSource newViewSource) {
	}

	@Override
	public void finish() {
		// Cleans up the image storage, so we will not clutter the device with unwanted images
		try {
			ImageStorage.cleanupImages();
		} catch (Exception e) {
			// If this fails, its no biggie, but something might be interesting
			ErrorHandler.justLogError(e);
		}
		super.finish();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        SharedPreferences prefs        = PreferenceManager.getDefaultSharedPreferences(this);
		galleryView.setColumnWidth(prefs.getInt(AppConstants.PREFERENCE_THUMB_SIZE, 130));
	}

	/* (non-Javadoc)
	 * @see com.sofurry.base.interfaces.IManagedActivity#getContentType()
	 */
	public ContentType getContentType() {
		return ContentType.art;
	}

// Following code show image info and scaled thumbnail on long click on submission in gallery
// for now have a better idea of preview so disabling this code for future use
// -= Night_Gryphon =-	
/*	
	private View OverlayInfo = null;
	
	@Override
	public boolean showPreview(int selectedIndex) {
		if (OverlayInfo != null) {
			return false;
		}
		
		Submission s = getDataItem(selectedIndex);
		Bitmap thumb = s.getThumbnail();
		
		if (thumb != null) {
			RelativeLayout main_view = (RelativeLayout) findViewById(R.id.gallery_main_layout);

			LayoutInflater mInflater = LayoutInflater.from(this);
			OverlayInfo = mInflater.inflate(R.layout.art_preview_overlay, null);
			main_view.addView(OverlayInfo);

			// text info
			TextView nfo = (TextView) OverlayInfo.findViewById(R.id.InfoText);
			if (nfo != null) {
				nfo.setText(s.getAuthorName()+": "+s.getName());
			}
			
			// image preview
			ImageView im = (ImageView) OverlayInfo.findViewById(R.id.PreviewImage);

			im.setImageBitmap(thumb);
			im.setScaleType(ImageView.ScaleType.FIT_CENTER);
	        im.setAdjustViewBounds(true);
	        
	        im.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
		    		RelativeLayout main_view = (RelativeLayout) findViewById(R.id.gallery_main_layout);
					main_view.removeView(OverlayInfo);

					ImageView im = (ImageView) OverlayInfo.findViewById(R.id.PreviewImage);
					if (im != null) {
						Drawable toRecycle = im.getDrawable();
			            if (toRecycle != null) {
			            	if ((toRecycle instanceof BitmapDrawable) && ( ((BitmapDrawable) toRecycle).getBitmap() != null )) {
			            		((BitmapDrawable) toRecycle).getBitmap().recycle();
			            	}
			                ((ImageView) v).setImageBitmap(null);
			            }
					}
					
					OverlayInfo = null;
				}
			});
		}

		return true;
	}
*/
	
}