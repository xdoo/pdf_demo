package de.muenchen.pdfdemo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;

@Service
@Slf4j
public class PdfService {

    @Value("classpath:fonts/roboto/Roboto-Thin.ttf")
    Resource robotoThin;

    @Value("classpath:fonts/roboto/Roboto-Thin.ttf")
    Resource robotoLight;

    @Value("classpath:fonts/roboto/Roboto-Regular.ttf")
    Resource robotoRegular;

    @Value("classpath:fonts/roboto/Roboto-Medium.ttf")
    Resource robotoMedium;

    @Value("classpath:fonts/roboto/Roboto-Bold.ttf")
    Resource robotoBold;

    @Value("classpath:fonts/roboto/Roboto-Black.ttf")
    Resource robotoBlack;

    private Mustache beleastungsplan;

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

        // fonts laden
        File thinFont = this.getFileFromResource(this.robotoThin);
        File lightFont = this.getFileFromResource(this.robotoLight);
        File regularFont = this.getFileFromResource(this.robotoRegular);
        File mediumFont = this.getFileFromResource(this.robotoMedium);
        File boldFont = this.getFileFromResource(this.robotoBold);
        File blackFont = this.getFileFromResource(this.robotoBlack);

        // PDF/A
        PdfRendererBuilder.PdfAConformance conform = PdfRendererBuilder.PdfAConformance.PDFA_1_A;

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        // PDF/A
        builder.usePdfVersion(conform.getPart() == 1 ? 1.4f : 1.5f);
        builder.usePdfAConformance(conform);
        // fonts einbinden
        builder.useFont(regularFont, "Roboto");
        builder.useFont(thinFont, "Roboto", 100,BaseRendererBuilder.FontStyle.NORMAL, true);
        builder.useFont(lightFont, "Roboto", 300,BaseRendererBuilder.FontStyle.NORMAL, true);
        builder.useFont(mediumFont, "Roboto", 500,BaseRendererBuilder.FontStyle.NORMAL, true);
        builder.useFont(boldFont, "Roboto", 700,BaseRendererBuilder.FontStyle.NORMAL, true);
        builder.useFont(blackFont, "Roboto", 900,BaseRendererBuilder.FontStyle.NORMAL, true);
        // sonstige konfiguration
        builder.withHtmlContent(html, "");
        builder.toStream(os);
        builder.useSVGDrawer(new BatikSVGDrawer());
        log.info("creating pdf");
        builder.run();

        return os.toByteArray();
    }

    @PostConstruct
    public void init() {
        log.info("initialisiere die Templates");
        MustacheFactory mf = new DefaultMustacheFactory();
        InputStream stream = this.getClass().getResourceAsStream("/templates/belastungsplan.mustache");
        InputStreamReader streamReader = new InputStreamReader(stream);
        this.beleastungsplan = mf.compile(streamReader, "chart");
    }

    /**
     * Das ist notwendig, um die Schriftdateien aus dem Jar laden zu können.
     *
     * @param resource
     * @return
     * @throws IOException
     */
    public File getFileFromResource(Resource resource) throws IOException {
        File tempFile = File.createTempFile(resource.getFilename(), ".ttf");
        FileUtils.copyInputStreamToFile(resource.getInputStream(), tempFile);
        return tempFile;
    }

}
