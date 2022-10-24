package me.banhammer.util;

import java.net.URL;
import java.util.HashMap;

public class HTTPRequest
{
	private eRequestType mRequestType;
	private URL mUrl;
	private String mUserAgent;
	private HashMap<String, String> mHeaders;
	private byte[] maPostData;
	private boolean mbRedirects = false;

	public HTTPRequest(URL lUrl, eRequestType lRequestType)
	{
		mRequestType = lRequestType;
		mUrl = lUrl;
		mUserAgent = "HTTPRequest/1.0.0";
		mHeaders = new HashMap<String, String>();
	}
	
	public HTTPResponse getResponse()
	{
		return new HTTPResponse(this);
	}
	
	public boolean willRedirect()
	{
		return mbRedirects;
	}

	public void setRedirects(boolean lbRedirects)
	{
		this.mbRedirects = lbRedirects;
	}
	
	public void setPostData(byte[] laPostData)
	{
		maPostData = laPostData;
	}
	
	public byte[] getPostData()
	{
		return maPostData;
	}
	
	public HashMap<String, String> getHeaders()
	{
		return mHeaders;
	}
	
	public String getHeader(String lsHeaderName)
	{
		return mHeaders.get(lsHeaderName);
	}
	
	public void setHeader(String lsHeaderName, String lsHeaderValue)
	{
		mHeaders.put(lsHeaderName, lsHeaderValue);
	}

	public eRequestType getRequestType()
	{
		return mRequestType;
	}

	public void setRequestType(eRequestType lRequestType)
	{
		this.mRequestType = lRequestType;
	}

	public URL getUrl()
	{
		return mUrl;
	}

	public void setUrl(URL lUrl)
	{
		this.mUrl = lUrl;
	}

	public String getUserAgent()
	{
		return mUserAgent;
	}

	public void setUserAgent(String lUserAgent)
	{
		this.mUserAgent = lUserAgent;
	}

	public enum eRequestType
	{
		POST, GET;
	}
}