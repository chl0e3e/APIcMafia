package me.banhammer.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import me.banhammer.util.HTTPRequest.eRequestType;

public class HTTPResponse
{
	private byte[] maResponse;
	private HTTPRequest mHttpRequest;
	private Map<String, List<String>> mResponseHeaders;
	private Throwable mError;
	private int mResponseCode;
	
	public HTTPResponse(HTTPRequest lHttpRequest)
	{
		mHttpRequest = lHttpRequest;
		downloadResponse();
	}
	
	public List<String> getResponseHeader(String lsKey)
	{
		return mResponseHeaders.get(lsKey);
	}
	
	private void downloadResponse()
	{
		try
		{
			HttpURLConnection lConnection = (HttpURLConnection) mHttpRequest.getUrl().openConnection();
			lConnection.setRequestMethod(mHttpRequest.getRequestType().name());
			lConnection.setDoInput(true);
			lConnection.setUseCaches(false);

			lConnection.setRequestProperty("User-Agent", mHttpRequest.getUserAgent());
			for(String lsKey : mHttpRequest.getHeaders().keySet())
			{
				String lsValue = mHttpRequest.getHeader(lsKey);
				lConnection.setRequestProperty(lsKey, lsValue);
			}
			
			if(mHttpRequest.getRequestType() == eRequestType.POST)
			{
				lConnection.setDoOutput(true);
				lConnection.setRequestProperty("Content-Length", Integer.toString(mHttpRequest.getPostData().length));

				DataOutputStream lDOS = new DataOutputStream(lConnection.getOutputStream ());
				lDOS.write(mHttpRequest.getPostData());
				lDOS.flush();
				lDOS.close();
			}
			lConnection.connect();
			InputStream lIS = lConnection.getInputStream();
			mResponseCode = lConnection.getResponseCode();
			mResponseHeaders = lConnection.getHeaderFields();
			maResponse = FileUtils.readBytes(lIS);
			lConnection.disconnect();
		}
		catch (Exception e)
		{
			mError = e;
		}
	}
	
	public byte[] getBytes()
	{
		return maResponse;
	}
	
	public String getString()
	{
		return new String(maResponse);
	}
	
	public String getString(String lsEncoding)
	{
		try
		{
			return new String(maResponse, lsEncoding);
		}
		catch (UnsupportedEncodingException e)
		{
			mError = e;
		}
		return null;
	}
	
	public boolean hasError()
	{
		return mError != null;
	}
	
	public Throwable getError()
	{
		return mError;
	}
}
