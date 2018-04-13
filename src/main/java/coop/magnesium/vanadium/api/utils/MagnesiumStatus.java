package coop.magnesium.vanadium.api.utils;

/**
 * Created by rsperoni on 20/11/17.
 */
public class MagnesiumStatus {

    public String project;
    public String version;
    public String node;
    public String swaggerUrl;
    public String docsUrl;

    public MagnesiumStatus(String project, String version, String node, String swaggerUrl, String docsUrl) {
        this.project = project;
        this.version = version;
        this.node = node;
        this.swaggerUrl = swaggerUrl;
        this.docsUrl = docsUrl;
    }

    @Override
    public String toString() {
        return "MagnesiumStatus{" +
                "project='" + project + '\'' +
                ", version='" + version + '\'' +
                ", node='" + node + '\'' +
                ", swaggerUrl='" + swaggerUrl + '\'' +
                ", docsUrl='" + docsUrl + '\'' +
                '}';
    }
}
