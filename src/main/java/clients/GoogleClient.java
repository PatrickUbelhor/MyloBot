package clients;

//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.SearchListResponse;
//import com.google.api.services.youtube.model.SearchResult;
//
//import java.io.IOException;
//import java.util.LinkedList;
//import java.util.List;
//
//import static main.Globals.logger;

public class GoogleClient {

	
//	public List<String> getVideos(String query) {
//		YouTube youtube = new YouTube.Builder().build();
//
//		try {
//			YouTube.Search.List searchListByKeywordRequest = youtube.search().list("snippet");
//			searchListByKeywordRequest.setQ(query);
//			searchListByKeywordRequest.setType("video");
//
//			SearchListResponse response = searchListByKeywordRequest.execute();
//			List<SearchResult> searchResults = response.getItems();
//
//			LinkedList<String> links = new LinkedList<>();
//			for (SearchResult result : searchResults) {
//				String videoId = result.getId().getVideoId();
//				links.addLast(videoId);
//			}
//
//			return links;
//		} catch (IOException e) {
//			logger.error(e);
//		}
//
//		return null;
//	}

}
