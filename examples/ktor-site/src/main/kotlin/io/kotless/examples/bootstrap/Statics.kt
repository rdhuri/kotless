package io.kotless.examples.bootstrap

import io.ktor.http.content.*
import io.ktor.routing.Routing
import kotlinx.html.*
import java.io.File


fun Routing.siteStatics() {
    //There are used almost all possible definitions of static resources

    static {
        staticRootFolder = File("src/main/static")

        static("css") {
            files("css")
        }

        static("js") {
            static("scripts") {
                file("highlight.pack.js", "js/highlight.pack.js")
            }
        }

        file("favicon.apng")
    }
}

private var HTMLTag.crossorigin: String
    get() = attributes["crossorigin"]!!
    set(value) {
        attributes["crossorigin"] = value
    }


fun HTML.headerSite() {
    head {
        title {
            +"Kotless Serverless Framework"
        }
        link("/favicon.apng", rel = "icon")
        link {
            href = "https://use.fontawesome.com/releases/v5.8.1/css/all.css"
            rel = "stylesheet"
            crossorigin = "anonymous"
            integrity = "sha384-50oBUHEmvpQ+1lW4y57PTFmhCaXp0ML5d60M1M7uH2+nqUivzIebhndOJK28anvf"
        }
        link {
            href = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
            rel = "stylesheet"
            crossorigin = "anonymous"
            integrity = "sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
        }
        link("/css/kotless-site.css", rel = "stylesheet")
        link("/css/highlight-style.css", rel = "stylesheet")
        script { src = "/js/scripts/highlight.pack.js" }
        script {
            src = "https://code.jquery.com/jquery-3.3.1.slim.min.js"
            crossorigin = "anonymous"
            integrity = "sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
        }
        script {
            src = "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
            crossorigin = "anonymous"
            integrity = "sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
        }
        script {
            src = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
            crossorigin = "anonymous"
            integrity = "sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
        }

        script(type = ScriptType.textJavaScript) {
            unsafe {
                raw("hljs.initHighlightingOnLoad();")
            }
        }
    }
}
