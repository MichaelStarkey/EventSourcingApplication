package main.mongo.branches;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.*;

public class EventTree {

    private HashMap<String, Branch> branches;
    private String currentBranch;

    public EventTree(){
        Branch branch = new Branch(0,"master");
        this.branches = new HashMap<>();
        this.branches.put("master", branch);
        this.currentBranch = "master";
    }

    public String getCurrentBranch(){
        return this.currentBranch;
    }

    public HashMap<String, Branch> getBranches(){
        return this.branches;
    }

    public EventTree(Document eventTree){
        HashMap<String, Branch> branches = new HashMap<>();
        this.currentBranch = eventTree.getString("currentBranch");
        Document docBranches = eventTree.get("branches", Document.class);

        for (String key : docBranches.keySet()) {
            Branch tempBranch = new Branch(docBranches.get(key, Document.class));
            branches.put(tempBranch.getName(), tempBranch);
        }

        this.branches = branches;
    }

    public Bson get_conditional(){
        Branch current_branch = this.branches.get(this.currentBranch);
        String branch_source = current_branch.getSource();
        long branch_start_time = current_branch.getBeginTimestamp();

        Bson cond = and(eq("branch", this.currentBranch), gte("timestamp", branch_start_time));
        ArrayList<Bson> conds = new ArrayList<>();
        conds.add(cond);

        while (branch_source != null) {
            current_branch = this.branches.get(branch_source);
            branch_source = current_branch.getSource();
            cond = and(
                eq("branch", current_branch.getName()),
                gte("timestamp", current_branch.getBeginTimestamp()),
                lt("timestamp", branch_start_time)
            );
            conds.add(cond);
            branch_start_time = current_branch.getBeginTimestamp();
        }

        return or(conds);
    }

    public Branch branch(String name, long timestamp) {
        Branch currentBranch = this.branches.get(this.currentBranch);
        Branch new_branch;
        while (true) {
            System.out.println(currentBranch.getBeginTimestamp());
            System.out.println(timestamp);
            if (timestamp >= currentBranch.getBeginTimestamp()) {
                new_branch = new Branch(timestamp, currentBranch.getName(), name);
                this.branches.put(name, new_branch);
                break;
            }
            currentBranch = this.branches.get(currentBranch.getSource());
            if (currentBranch.getSource() != null) return null;
        }

        return new_branch;
    }

    public void checkout(String name) {
        Branch branch = this.branches.get(name);
        if (branch != null){
            this.currentBranch = name;
        }
    }

    public Document toDoc() {
        Document branches = new Document();
        for (String key : this.branches.keySet()) {
            branches.append(key, this.branches.get(key).toDoc());
        }
        return new Document("currentBranch", this.currentBranch).append("branches", branches);
    }
}
