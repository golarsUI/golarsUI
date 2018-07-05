package com.golars.util;

import java.util.List;

import com.golars.bean.Folder;

public class GolarsUtil {
	public static Folder getChildren(Folder currentNode, List<Folder> folderList) {
		for (Folder pair : folderList) {
			String parentid = currentNode.getParentid().equalsIgnoreCase("Null")?currentNode.getId()+"":currentNode.getParentid()+currentNode.getId()+"";
			if (pair.getParentid().equals(parentid)) {
				currentNode.getChildren().add(pair);
				if (pair.getParentid() != null) {
					getChildren(pair, folderList);
				}

			}

		}
		return currentNode;

	}

	public static Folder getCurrentNode(int fId, List<Folder> folderList) {
		for (Folder pair : folderList) {
			if (pair.getId() == fId)
				return pair;
		}
		return null;
	}

}