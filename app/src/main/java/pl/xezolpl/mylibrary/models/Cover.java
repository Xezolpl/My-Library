package pl.xezolpl.mylibrary.models;

import java.net.URL;

public class Cover {
    private URL url;

    public Cover(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
