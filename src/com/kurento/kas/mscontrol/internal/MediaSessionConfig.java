package com.kurento.kas.mscontrol.internal;

import java.awt.Dimension;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

import com.kurento.commons.mscontrol.Configuration;
import com.kurento.commons.mscontrol.MediaSession;
import com.kurento.commons.sdp.enums.MediaType;
import com.kurento.commons.sdp.enums.Mode;
import com.kurento.kas.media.codecs.AudioCodecType;
import com.kurento.kas.media.codecs.VideoCodecType;
import com.kurento.kas.mscontrol.networkconnection.NetIF;

public class MediaSessionConfig implements Configuration<MediaSession> {

	private NetIF netIF;
	private InetAddress localAddress;
	private InetAddress publicAddress;
	private Integer maxBW;

	private Map<MediaType, Mode> mediaTypeModes;
	private ArrayList<AudioCodecType> audioCodecs;
	private ArrayList<VideoCodecType> videoCodecs;

	private Dimension frameSize;
	private Integer maxFrameRate;
	private Integer gopSize;
	private Integer framesQueueSize;

	public NetIF getNetIF() {
		return netIF;
	}

	public InetAddress getLocalAddress() {
		return localAddress;
	}
	
	public InetAddress getPublicAddress() {
		return publicAddress;
	}

	public Integer getMaxBW() {
		return maxBW;
	}

	public Map<MediaType, Mode> getMediaTypeModes() {
		return mediaTypeModes;
	}

	public ArrayList<AudioCodecType> getAudioCodecs() {
		return audioCodecs;
	}

	public ArrayList<VideoCodecType> getVideoCodecs() {
		return videoCodecs;
	}

	public Dimension getFrameSize() {
		return frameSize;
	}

	public Integer getMaxFrameRate() {
		return maxFrameRate;
	}

	public Integer getGopSize() {
		return gopSize;
	}

	public Integer getFramesQueueSize() {
		return framesQueueSize;
	}

	protected MediaSessionConfig(NetIF netIF, InetAddress localAddress, InetAddress publicAddress,
			Integer maxBW, Map<MediaType, Mode> mediaTypeModes,
			ArrayList<AudioCodecType> audioCodecs,
			ArrayList<VideoCodecType> videoCodecs, Dimension frameSize,
			Integer maxFrameRate, Integer gopSize, Integer framesQueueSize) {
		this.netIF = netIF;
		this.localAddress = localAddress;
		this.publicAddress= publicAddress;
		this.maxBW = maxBW;

		this.mediaTypeModes = mediaTypeModes;
		this.audioCodecs = audioCodecs;
		this.videoCodecs = videoCodecs;

		this.frameSize = frameSize;
		this.maxFrameRate = maxFrameRate;
		this.gopSize = gopSize;
		this.framesQueueSize = framesQueueSize;
	}


}
