package com.address.recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author jayant
 * very basic search; needs optimization for larger datasets
 *
 */
public class AddressRecommender {
	
	//Map having all the addresses with keywords
	static Map<String, Set<String>> addressMap = new HashMap<>();

	public static void main(String[] args) {
		// input addresses for repository
		List<String> addresses = new ArrayList<String>();
		addresses.add("6480, Sector C6, Vasant Kunj");
	    addresses.add("Plot 16, Udyog Vihar Phase -4, Gurgaon");
	    addresses.add("8231, Sector C8, Vasant Kunj");
	    addresses.add("C-6/6280, Vasant Kunj");
	    
	    //creating address map for faster search
	    for(String address : addresses) {
	    	List<String> editedAddresses = editAddressComponents(address);
	    	
	    	for(String component : editedAddresses) {
	    		Set<String> componentSet = addressMap.get(component);
	    		if(addressMap.get(component) == null) {
	    			componentSet = new HashSet<>();	
	    		}
	    		componentSet.add(address);
    			addressMap.put(component, componentSet);
	    	}
	    }
	    
	    
	    System.out.println(getSimilarAddress("6279, Sector C6, Vasant Kunj"));
	    
	    System.out.println(getSimilarAddress("Plot 18, National Highway  8, Udyog Vihar Phase -4, Gurgaon"));
	    
	}
	
	private static String getSimilarAddress(String address) {
		List<String> components = editAddressComponents(address);
		Map<String, Integer> addressMatches = new HashMap<>();
		int maxCount = 0;
		List<String> similarAddresses = new ArrayList<>();
		for(String component : components) {
			Set<String> storedComponents = addressMap.get(component);
			if (storedComponents != null) {
				for (String storedComponent : storedComponents) {
					int count = 0;
					if (addressMatches.containsKey(storedComponent)) {
						count = addressMatches.get(storedComponent);
					}
					count++;
					if (count == maxCount) {
						similarAddresses.add(storedComponent);
					} else if (count > maxCount) {
						similarAddresses.clear();
						similarAddresses.add(storedComponent);
					}
					addressMatches.put(storedComponent, count);
				} 
			}
		}
		return getMostSimilarAddress(similarAddresses);
	}
	
	//can be optimized further to get most similar address as well as to update the repository
	private static String getMostSimilarAddress(List<String> similarAddresses) {
		return similarAddresses.isEmpty() ? "" : similarAddresses.get(0);
	}

	private static List<String> editAddressComponents(String address) {
		address = address.replaceAll("/", ",");
		String[] addressArray = address.split(",");
		List<String> modifiedAddressArray = new ArrayList<>();
		for(String component : addressArray) {
			component = editSpecialCharacters(component);
			modifiedAddressArray.add(component);
		}
		return modifiedAddressArray;
	}
	
	private static String removeUnnecessaryWords(String address) {
		address = address.toLowerCase();
		String[] components = address.split(" ");
		String modifiedComponent = "";
		List<String> unnecessaryWordList = unnecessaryWordList();
		for(String component : components) {
			if(!unnecessaryWordList.contains(component.trim())) {
				modifiedComponent += " " + component.trim();
			}
		}
		modifiedComponent = modifiedComponent.trim();
		return modifiedComponent;
	}
	
	//should be DB configured and stored in cache
	private static List<String> unnecessaryWordList() {
		List<String> wordList = new ArrayList<>();
		wordList.add("house");
		wordList.add("no");
		wordList.add("number");
		wordList.add("sector");
		wordList.add("plot");
		wordList.add("town");
		wordList.add("pin");
		wordList.add("code");
		wordList.add("pincode");
		return wordList;
	}

	private static String editSpecialCharacters(String address) {
		address = removeUnnecessaryWords(address);
		Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match= pt.matcher(address);
        while(match.find()) {
            String s = match.group();
            address=address.replaceAll("\\"+s, "");
        }
        address = address.toLowerCase();
        return address;
	}

}
