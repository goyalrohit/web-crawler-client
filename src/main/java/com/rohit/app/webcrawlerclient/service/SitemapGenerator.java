package com.rohit.app.webcrawlerclient.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SitemapGenerator {
    private static Node root;

    public void createSiteMap(String url) {
        // Build the sitemap tree structure
        System.out.println("URL- " + url);
        root = buildSitemap(url);
        printSitemap(root, "");
    }

    private static Node buildSitemap(String url) {
        Node rootNode = new Node("");

        //for (String url : urls) {
            String[] parts = url.split("/");
            Node currentNode = rootNode;

            for (int i = 2; i < parts.length; i++) {
                String part = parts[i];
                Node child = findChild(currentNode, part);

                if (child == null) {
                    child = new Node(part);
                    currentNode.addChild(child);
                }

                currentNode = child;
            }
     //   }

        return rootNode;
    }

    private static Node findChild(Node parent, String childUrl) {
        for (Node child : parent.getChildren()) {
            if (child.getUrl().equals(childUrl)) {
                return child;
            }
        }
        return null;
    }

    private static void printSitemap(Node node, String indent) {
        if (!node.getUrl().isEmpty()) {
            System.out.println(indent + "-" +  node.getUrl());
        }

        for (Node child : node.getChildren()) {
            printSitemap(child, indent + "  ");
        }
    }
}

class Node {
    private String url;
    private List<Node> children;

    public Node(String url) {
        this.url = url;
        this.children = new ArrayList<>();
    }

    public String getUrl() {
        return url;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }
}
