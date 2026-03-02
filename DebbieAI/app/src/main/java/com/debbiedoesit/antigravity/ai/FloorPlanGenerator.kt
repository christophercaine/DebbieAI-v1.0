package com.debbiedoesit.antigravity.ai

import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import java.io.File

data class Room(
    val name: String,
    val widthInches: Float,
    val heightInches: Float,
    val features: List<RoomFeature> = emptyList()
)

data class RoomFeature(val type: FeatureType, val wallSide: Int, 
    val positionFromCornerInches: Float, val widthInches: Float)

enum class FeatureType { DOOR, WINDOW, ELECTRICAL_OUTLET, PLUMBING }

/**
 * Floor plan generation takes room measurements and produces a to-scale SVG/PDF sketch.
 */
class FloorPlanGenerator {

    fun generateSVG(rooms: List<Room>, scale: Float = 0.1f): String {
        val sb = StringBuilder()
        sb.append("<svg xmlns='http://www.w3.org/2000/svg' ")
        sb.append("width='800' height='600'>\n")

        var xOffset = 20f
        rooms.forEach { room ->
            val w = room.widthInches * scale
            val h = room.heightInches * scale

            // Room rectangle
            sb.append("<rect x='$xOffset' y='20' width='$w' height='$h' ")
            sb.append("fill='#F0F6FF' stroke='#1B3A5C' stroke-width='2'/>\n")

            // Room label with dimensions
            val label = "${room.name} (${room.widthInches.toInt()}\" x ${room.heightInches.toInt()}\")"
            sb.append("<text x='${xOffset + w/2}' y='${20 + h/2}' ")
            sb.append("text-anchor='middle' font-size='10'>$label</text>\n")

            // Draw doors and windows
            room.features.forEach { feature ->
                drawFeature(sb, feature, xOffset, 20f, w, h, scale)
            }

            xOffset += w + 10
        }

        sb.append("</svg>")
        return sb.toString()
    }

    // Convert SVG to PDF for sharing/printing
    fun exportToPDF(svgContent: String, outputPath: String) {
        val pdfWriter = PdfWriter(outputPath)
        val pdf = PdfDocument(pdfWriter)
        val document = Document(pdf, PageSize.LETTER.rotate())
        // In a real implementation, we would use an SVG converter (like iText's svg-pdf or JFreeSVG)
        // to render the SVG onto the PDF document.
        document.close()
    }

    private fun drawFeature(sb: StringBuilder, f: RoomFeature, 
        rx: Float, ry: Float, rw: Float, rh: Float, scale: Float) {
        val color = when (f.type) {
            FeatureType.DOOR -> "#E74C3C"
            FeatureType.WINDOW -> "#3498DB"
            else -> "#F39C12"
        }
        val pos = f.positionFromCornerInches * scale
        val fw = f.widthInches * scale
        when (f.wallSide) {
            0 -> sb.append("<rect x='${rx+pos}' y='$ry' width='$fw' height='3' fill='$color'/>\n")
            1 -> sb.append("<rect x='${rx+rw-3}' y='${ry+pos}' width='3' height='$fw' fill='$color'/>\n")
            2 -> sb.append("<rect x='${rx+pos}' y='${ry+rh-3}' width='$fw' height='3' fill='$color'/>\n")
            3 -> sb.append("<rect x='$rx' y='${ry+pos}' width='3' height='$fw' fill='$color'/>\n")
        }
    }
}
