package de.muenchen.pdfdemo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;

@Service
@Slf4j
public class PdfService {

    private Mustache beleastungsplan;

    public PdfService() {
        this.init();
    }

    public String createDiagramFile(BelastungsplanPdf bean) {
        StringWriter writer = new StringWriter();
        this.beleastungsplan.execute(writer, bean);
        return writer.toString();
    }

    /**
     * Erzeugt ein PDF
     *
     * @param html
     * @return
     * @throws IOException
     */
    public byte[] createPdf(String html) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, "");
        builder.toStream(os);
        builder.useSVGDrawer(new BatikSVGDrawer());
        log.info("creating pdf");
        builder.run();

        return os.toByteArray();
    }

    public void init() {
        log.info("initialisiere die Templates");
        MustacheFactory mf = new DefaultMustacheFactory();
        InputStream stream = this.getClass().getResourceAsStream("/templates/belastungsplan.mustache");
        InputStreamReader streamReader = new InputStreamReader(stream);
        this.beleastungsplan = mf.compile(streamReader, "chart");
    }

    public String getWappen() {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 30 30\" width=\"100%\" height=\"100%\">\n" +
                "<path stroke=\"#000\" stroke-width=\"4.8\" d=\"m3 161.5c0 27.4 11.25 54.6 30.62 74s46.62 30.6 73.98 30.6c27.4 0 54.6-11.3 74-30.6 19.4-19.4 30.6-46.6 30.6-74v-158.5l-209.2-0.025z\" fill=\"#fff\"/>\n" +
                "<path d=\"m183.5 51.3 9.3 18.98-13 7.05-9.2-19.43z\" fill=\"#c20000\"/>\n" +
                "<path d=\"m165 80.1h7.8l6.6-3.75-3.9-8.26 6.9-4.74-0.9-2.25-8.7 5\"/>\n" +
                "<path d=\"m37 61.2-7.9-7.48-2.62 5.5 10.52 4.88m10.6 7-8.38-16.5-1.22 6.6 9.6 18.9m-7.6-16.9h-4c-1.64 2.89-3.55 5.71-4.4 8l8.4 1\"/>\n" +
                "<path d=\"m93.82 34.4c-0.64 1.29 3.42 0.38 3.92 1.79 0.5 1.42 4.49 27.9 4.49 27.9l-2.29 2.48v6.4h11.01v-37.08c-5.1-0.38-9.2-3.49-17.08-1.49z\" fill=\"#FFE600\"/>\n" +
                "<path d=\"m94.25 34.72c-0.25 0.02-0.51 0.06-0.75 0.12-7.37 3.25-3.64 10.38-5.88 21.5l2.38 0.5c0 2.37 0.38 3.87 0.72 6.75 6.63 0.62 10.38-0.25 10.88 0 1.7 1.5 4.3 6.88 4.3 6.88l0.5-0.25s0.9-24.38 0.2-26.38c-0.6-3.12-4.1-6-6.72-7.62-1.76-0.88-3.86-1.66-5.63-1.5zm-65.16 19-1.12 2c4 3.12 6.62 4.12 8.12 5.5l-0.5 1.25c-2.12-1-7.12-4.26-8.25-4.88l-0.87 1.63c3.88 2.74 7.5 4.63 8.25 5.25 2.13 1.13 4.13 1.12 5.25 2.25v1.62c-2.88-0.62-4.75-2.38-6-1.62l-0.88 1.25c2.13 0 5.38 2.12 5.38 2.12l-0.5 1.5c-2.5-0.87-2.73-1.63-5.59-2.25l-1 2.25 23 12.38-0.07-12.28c-0.74-0.23-1.46-0.63-2.09-0.97-3.63-2.25-1.63-6-2.13-7.25 0-0.25-7.86-6.76-10.87-8.88-2.5 3.5 2.87 5.88 3.87 7.63 1.75 2.62-0.12 6.75 4.38 9.5l-0.13 2c-6.75-1.88-4.5-8.26-5.75-10.63-1.25-1.5-2.25-2-4-3.25-3.27-2.25-5.37-4.24-8.5-6.12zm150.5 5.34c-0.2-0.01-0.6 0.08-1 0.28l-6.1 3.07c-2.5 1.5-4.2 2.4-4.2 2.4-2.4 1.3 1.9 5.49-1.4 6.72-1.5 0.58-2.9 1.2-3.9 1.47v13.2l14.5-8.79-2.3-4.79c-1.1 3.4-2.9 4.5-6.1 5.1l-0.5-2c2.6-0.8 4.3-1.39 5-4.19 0.4-2.1-1-3.1-0.1-5 0.1-0.5 3.7-2.91 6.1-4.41 0.8-0.5 1.6-0.89 1.9-1.09-0.7-1.07-1.1-1.94-1.9-1.97z\" fill=\"#f9b385\"/>\n" +
                "<path d=\"m100.3 33.72c5.7 2.7 6.5 3.79 8.7 6.69 1.3 2.6 1.2 5 1.2 15.4 0 6.7-0.6 13.11-0.5 13.41l-6.8 1.9c-0.6-0.2-1.8-3.29-2.9-4.59-6 6.8-15.17 8.68-23.25 9.28-4.56 0.6-17.72-2.1-23-4.4 0 0-0.27 86.59-0.28 126.4 7.3 0 12.65-2.3 16.94-9.1 1.07-1.3 2.34-3.8 2.34-15.3 0-12 0.09-57.4 0.09-57.4 0-2.8 1.91-6.5 4.91-9.3 2.31-2.2 4.07-3.1 7.16-3.9v94.2l48.09 0.2v-95.8c2.4 0.8 5.8 3.2 7.3 5.4 1.5 2.1 3.3 4.4 4.6 9.1 1.3 6 0.7 56.3 0.7 57.4 0 8-0.7 14.2 2.7 19.1 3 4.1 11.1 6.2 15.6 6.6v-126.3c-9.3 3.12-20.3 4.31-28.8 2.61-7.3-1.3-12.3-6.8-12.8-7.9-3.8-7.7 13.9-5.79 21.5-5.79-1-5.7-1.9-7.2-4-11.5-4.2-7.2-7.9-10.4-14.7-13.4-6-2.3-18.8-3-24.8-3z\"/>\n" +
                "<path d=\"m110.8 91.72c8.625-0.25 15-3.375 18.62-7.125 2.125-2 4-3.875 4.75-6.75l-3.5-1.375c-0.625 2.25-1.75 3.375-3.5 5-3.375 3.5-10.38 6.5-18.5 6.5-6.75 0-12.75-2.25-16.5-4.75-2.875-1.875-4.75-3.625-5.75-6.75l-3.45 1.62c1 3.625 3.75 6.25 7.125 8.375 4 2.75 9.25 5 16.12 5.25v35.37h-19.2v4.25h19.25v63.62h4.5v-63.6h20.12v-4.25h-20v-35.48z\" fill=\"#FFE600\"/>\n" +
                "<path stroke=\"#c20000\" d=\"m91.91 200.7c-1.29 0.4-1.91 4.3-3.41 6.2-1.94 2.5-10.66 7.8-10.66 18.3 5.79-3.7 8.28-5.5 14.28-9.8 4.88-3.4 7-3.9 7.44-4.5v-10.2h-7.65zm24.69 0c0.1 6.5 0 9.1 0.6 10.2 1.1 1.5 3.4 1.7 8.6 5.6 4.8 3.8 7.4 5.9 12.3 8.7-0.9-11.7-3.6-10.9-7.9-15.8-2.3-2.1-5.5-8.5-5.7-8.7h-7.9z\" fill=\"#c20000\"/>\n" +
                "<g fill=\"#231F20\">\n" +
                "<path d=\"m102.8 43.1c0.1-2.25-0.4-2.5-2.4-4.5-0.75 2.38-0.4 3.25 2.4 4.5zm-2.92 0.12c0.12-2.24 0.32-2.86-2-4.62-0.62 2.38-0.62 3.12 2 4.62zm-2.88-0.24c0.25-2.26 0.75-2.64-2-4.63-0.62 2.37-0.5 3.5 2 4.63zm-3.18 0.25c0.76-2.01 1.26-3.38-1.87-4.75-0.37 2.75 0.25 3 1.87 4.75zm-0.2 5.74c3.38 1.75 3.62 1.75 6.88-0.37-1-1.88-2.76-3.13-5-2.63-1.26 0.62-1.76 1.37-1.88 2.74z\"/>\n" +
                "</g>\n" +
                "</svg>";
    }


}