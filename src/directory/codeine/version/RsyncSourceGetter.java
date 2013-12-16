package codeine.version;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import codeine.api.NodeGetter;
import codeine.jsons.info.VersionInfo;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.utils.StringUtils;
import codeine.utils.network.InetUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


public class RsyncSourceGetter {

	private @Inject VersionsMapping versionsMapping;
	private @Inject NodeGetter nodesGetter;
	private Random random = new Random();
	
	public String getForVersion(String version, final String hostname) {
		VersionInfo info = versionsMapping.info(version);
		if (null == info){
			return null;
		}
		final String versionName = info.name;
		final String hostDomain = InetUtils.domain(hostname);
		
		
		
		//to overcome permissions
//		if ("a".equals("a")){
//			return versionsMapping.info(versionName).default_rsync_source;
//		}
		List<PeerStatusJsonV2> filteredByVersion = Lists.newArrayList(Iterables.filter(nodesGetter.peers(), new Predicate<PeerStatusJsonV2>() {
			@Override
			public boolean apply(PeerStatusJsonV2 peerStatus){
				return versionName.equals(peerStatus.version()) && !hostname.equals(peerStatus.host())
						&& !StringUtils.isEmpty(peerStatus.tar());
			}
		}));
		List<PeerStatusJsonV2> filteredByDomain = Lists.newArrayList(Iterables.filter(filteredByVersion, new Predicate<PeerStatusJsonV2>() {
			@Override
			public boolean apply(PeerStatusJsonV2 peerStatus){
				return hostDomain.equals(InetUtils.domain(peerStatus.host())) && !hostname.equals(peerStatus.host());
			}
		}));
		if (!filteredByDomain.isEmpty()){
			String hostUrl = hostUrl(filteredByDomain);
			if (null != hostUrl){
				return hostUrl;
			}
		}
		if (!filteredByVersion.isEmpty()){
			String hostUrl = hostUrl(filteredByVersion);
			if (null != hostUrl){
				return hostUrl;
			}
		}
		
		return versionsMapping.info(versionName).default_rsync_source;
	}

	private String hostUrl(List<PeerStatusJsonV2> list) {
		int nextInt = random.nextInt(list.size() + 1);
		if (nextInt == list.size()){
			return null;// with decreasing probability - don't use that list to prevent bad version somewhere affecting
		}
		PeerStatusJsonV2 peerStatusJson = list.get(nextInt);
		return peerStatusJson.host() + ":" + peerStatusJson.tar();
	}

}
