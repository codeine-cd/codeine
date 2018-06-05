package codeine.version;

import codeine.api.NodeGetter;
import codeine.jsons.info.VersionInfo;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.utils.StringUtils;
import codeine.utils.network.InetUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;


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
		List<PeerStatusJsonV2> filteredByVersion = Lists.newArrayList(Iterables.filter(nodesGetter.peers(),
			peerStatus -> versionName.equals(peerStatus.version()) && !hostname.equals(peerStatus.canonical_host())
					&& !StringUtils.isEmpty(peerStatus.tar())));
		List<PeerStatusJsonV2> filteredByDomain = Lists.newArrayList(Iterables.filter(filteredByVersion,
			peerStatus ->
				hostDomain.equals(InetUtils.domain(peerStatus.canonical_host())) && !hostname.equals(peerStatus.canonical_host())));
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
		return peerStatusJson.canonical_host() + ":" + peerStatusJson.tar();
	}

}
