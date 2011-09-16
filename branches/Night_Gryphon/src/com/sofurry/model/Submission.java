package com.sofurry.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sofurry.AppConstants;
import com.sofurry.base.interfaces.IHasThumbnail;
import com.sofurry.requests.ContentDownloader;
import com.sofurry.requests.HttpRequest;
import com.sofurry.storage.FileStorage;
import com.sofurry.storage.ImageStorage;

public class Submission implements Serializable, IHasThumbnail {

	private static final long serialVersionUID = -3841250259233075462L;

	public enum SUBMISSION_TYPE {ARTWORK, STORY, JOURNAL, MUSIC};
	
	private SUBMISSION_TYPE type;
	private int id = -1;
	private String name;
	private String content;
	private String tags;
	private String authorName;
	private int authorID;
	private String contentLevel;
	private String date;
	private String thumbnailUrl;
	private String SavedNameCache = "";

	private byte attempts = 0;
	
	public SUBMISSION_TYPE getType() {
		return type;
	}
	public void setType(SUBMISSION_TYPE type) {
		this.type = type;
	}
	/* (non-Javadoc)
	 * @see com.sofurry.model.IHasThumbnail#getId()
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public int getAuthorID() {
		return authorID;
	}
	public void setAuthorID(int authorID) {
		this.authorID = authorID;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContentLevel() {
		return contentLevel;
	}
	public void setContentLevel(String contentLevel) {
		this.contentLevel = contentLevel;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
//	public String getFilenameUrl() {
//		return filenameUrl;
//	}
	/* (non-Javadoc)
	 * @see com.sofurry.model.IHasThumbnail#getThumbnail()
	 */
	public Bitmap getThumbnail() {
//		return thumbnail;
		return loadIconFromStorage();
	}
	
	public Boolean checkThumbnail() {
		if (type == SUBMISSION_TYPE.ARTWORK) {
			return ImageStorage.checkSubmissionIcon(getId());
		} else {
			return ImageStorage.checkUserIcon(getAuthorID());
		}
	} /**/

/*	public void loadIconFromStorage() {
		if (type == SUBMISSION_TYPE.ARTWORK) {
			thumbnail = ImageStorage.loadSubmissionIcon(getId());
		} else {
			thumbnail = ImageStorage.loadUserIcon(getAuthorID());
		}
	} /**/
	
	public Bitmap loadIconFromStorage() {
		if (type == SUBMISSION_TYPE.ARTWORK) {
			return ImageStorage.loadSubmissionIcon(getId());
		} else {
			return ImageStorage.loadUserIcon(getAuthorID());
		}
	} /**/

/*	public void storeIcon() throws Exception {
		if (type == SUBMISSION_TYPE.ARTWORK) {
			ImageStorage.saveSubmissionIcon(getId(), thumbnail);
		} else {
			ImageStorage.saveUserIcon(getAuthorID(), thumbnail);
		}
	} /**/
	
	/**
	 * Downloads the thumbnail for this submission
	 */
/*	public void populateThumbnail(boolean fastmode) throws Exception {
		if (getId() == -1) return;

		
		// See if we have the image in storage
		loadIconFromStorage();
		
		if (fastmode) return; // In fastmode we will not try downloading the thumb.
		
		if (thumbnail == null) {
		  Log.i(AppConstants.TAG_STRING, "ThumbDownloader: Downloading thumb for pid " + getId() + " from " + thumbnailUrl);
		  thumbnail = ContentDownloader.downloadBitmap(thumbnailUrl);
		  Log.i(AppConstants.TAG_STRING, "ThumbDownloader: Storing image");
		  storeIcon();
		}
	}/**/

	public void populateThumbnail(boolean fastmode) throws Exception {
		if (getId() == -1) return;
		if (fastmode) return; // In fastmode we will not try downloading the thumb.

		if (type == SUBMISSION_TYPE.ARTWORK) {
			if (!ImageStorage.checkSubmissionIcon(getId()) ) {
				ContentDownloader.downloadFile2(thumbnailUrl, ImageStorage.getSubmissionIconPath2(getId()), null);
			}
		} else {
			if (! ImageStorage.checkUserIcon(getAuthorID()) ) {
				ContentDownloader.downloadFile2(thumbnailUrl, ImageStorage.getUserIconPath2(getAuthorID()), null);
			}
		}
			
	}
	
	
	/**
	 * Returns the number of attempts that were used to get the thumbnail
	 * @return
	 */
	public byte getThumbAttempts() {
		return attempts++;
	}
	
    /**
     * Build absolute file name to save 
     * @throws Exception 
     */
    public String getSaveName(Context context) throws Exception {
        // Image filename template (Issue 38) by NGryph
        // load template from preferences
    	if (SavedNameCache.length() > 0) {
    		return SavedNameCache;
    	}
    	
        SharedPreferences prefs        = PreferenceManager.getDefaultSharedPreferences(context);
        String            fileNameTmpl = prefs.getString(AppConstants.PREFERENCE_IMAGE_FILE_NAME_TMPL,
                                                         "%AUTHOR% - %NAME%");
        boolean           useOnlyAdult = prefs.getBoolean(AppConstants.PREFERENCE_IMAGE_TMPL_USE_ONLY_ADULT, false);

        // filename must have correct extension. not sure is it true or not
//        String targetPath = fileNameTmpl + '.' + filename.substring(filename.lastIndexOf('.') + 1);
        String targetPath = fileNameTmpl + HttpRequest.extractExtension(getThumbnailUrl());

        /*
         *  sanitizeFileName removes '/' character so separately sanitize every unsecure data field that comes from
         * the web. We're also making sure that dots are filtered out of these as well, simply because we want to
         * avoid accidental hacks due to weird names.
         */
        targetPath = targetPath.replaceAll("%AUTHOR%", FileStorage.sanitizeFileName(getAuthorName(), true));
        targetPath = targetPath.replaceAll("%NAME%", FileStorage.sanitizeFileName(getName(), true));
        targetPath = targetPath.replaceAll("%DATE%", FileStorage.sanitizeFileName(getDate(), true));
        targetPath = targetPath.replaceAll("%ID%", FileStorage.sanitizeFileName(""+getId(), true));

        // Determine the level
        if (getContentLevel().equals("0")) {
            targetPath = targetPath.replaceAll("%LEVEL%", "clean");
        } else if ((getContentLevel().equals("1")) || (useOnlyAdult)) {
            targetPath = targetPath.replaceAll("%LEVEL%", "adult");
        } else {
            targetPath = targetPath.replaceAll("%LEVEL%", "extreme");
        }

        // some kind of hack. getUserStoragePath performs sanitize on filename and broke '/' chars from template
        // let's treat that getUserStoragePath provide root dir for image lib when filename is empty
       	targetPath = FileStorage.getUserStoragePath("Images", "") + targetPath;

       	SavedNameCache = targetPath;
       	return targetPath;
    }

    // build relative file name to look in cache
    public String getCacheName() {
        String filename = getName() + HttpRequest.extractExtension(getThumbnailUrl());
        filename = FileStorage.sanitize(filename);
    	return filename;
    }

    public String getPreviewURL() {
		return thumbnailUrl.replace("/thumbnails/", "/preview/");
	}
	
	public String getFullURL() {
		return thumbnailUrl.replace("/art/thumbnails/", "/content/" + getId() + ".jpg/");
	}
	
	/**
	 * Populates the Submission with data from a JSON object
	 * @param datasource
	 * The object to extract the data from
	 * @throws Exception
	 */
	public void populate(JSONObject datasource) throws JSONException {
		setName(datasource.getString("name"));
		setId(Integer.parseInt(datasource.getString("pid")));
		setDate(datasource.getString("date"));
		setAuthorName(datasource.getString("authorName"));
		setAuthorID(Integer.parseInt(datasource.getString("authorId")));
		setContentLevel(datasource.getString("contentLevel"));
		setTags(datasource.getString("keywords"));
		setThumbnailUrl(datasource.getString("thumb"));
//		if (type == SUBMISSION_TYPE.MUSIC) {
//			filenameUrl = datasource.getString("filename");
//		}
	}
	
	/**
	 * Adds the submissions extra data to an intent
	 * @param intent
	 */
	public void feedIntent(Intent intent) {
		intent.putExtra("submission", this);
		intent.putExtra("pageID", getId());
		intent.putExtra("name", getName());
		intent.putExtra("tags", getTags());
		intent.putExtra("authorName", getAuthorName());
		intent.putExtra("authorId", getAuthorID());
		intent.putExtra("thumbnail", getThumbnailUrl());
		intent.putExtra("date", getDate());
		intent.putExtra("level", getContentLevel());
	}
	
}