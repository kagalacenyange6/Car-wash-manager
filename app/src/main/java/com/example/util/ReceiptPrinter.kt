package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.example.data.model.Company
import com.example.data.model.Payment
import com.example.data.model.WashOrder
import com.example.data.model.WashOrderItem
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReceiptPrinter {

    fun generateThermalText(
        order: WashOrder,
        items: List<WashOrderItem>,
        payments: List<Payment>,
        company: Company,
        paperWidth: String = "80mm"
    ): String {
        val widthCols = if (paperWidth == "58mm") 32 else 44
        val divider = "-".repeat(widthCols)
        val doubleDivider = "=".repeat(widthCols)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
        val orderDate = Date(order.createdAt)

        val sb = StringBuilder()

        fun center(text: String): String {
            val trimmed = text.take(widthCols)
            val padding = ((widthCols - trimmed.length) / 2).coerceAtLeast(0)
            return " ".repeat(padding) + trimmed
        }

        fun row(left: String, right: String): String {
            val maxLeft = widthCols - right.length - 1
            val l = if (left.length > maxLeft) left.take(maxLeft) else left
            val spaces = (widthCols - l.length - right.length).coerceAtLeast(1)
            return l + " ".repeat(spaces) + right
        }

        sb.appendLine(doubleDivider)
        sb.appendLine(center(company.name.uppercase()))
        sb.appendLine(center(company.address))
        sb.appendLine(center("Tél: ${company.phone}"))
        if (company.receiptHeaderNote.isNotBlank()) {
            sb.appendLine(center(company.receiptHeaderNote))
        }
        sb.appendLine(doubleDivider)

        sb.appendLine(row("FACTURE N°:", order.orderNumber))
        sb.appendLine(row("Date:", dateFormat.format(orderDate)))
        sb.appendLine(row("Heure:", timeFormat.format(orderDate)))
        sb.appendLine(divider)

        sb.appendLine(row("CLIENT:", order.customerName))
        sb.appendLine(row("Téléphone:", order.customerPhone))
        sb.appendLine(row("VÉHICULE:", order.vehicleModel))
        sb.appendLine(row("Immatriculation:", order.vehiclePlate))
        sb.appendLine(divider)

        sb.appendLine(row("SERVICES", "PRIX (${company.currencySymbol})"))
        sb.appendLine(divider)

        if (items.isNotEmpty()) {
            items.forEach { item ->
                sb.appendLine(row(item.serviceName, String.format(Locale.US, "%.2f", item.price)))
            }
        } else {
            sb.appendLine(row(order.servicesSummary, String.format(Locale.US, "%.2f", order.totalAmount)))
        }

        sb.appendLine(divider)
        sb.appendLine(row("SOUS-TOTAL:", String.format(Locale.US, "%.2f %s", order.totalAmount, company.currencySymbol)))
        if (order.discountAmount > 0) {
            sb.appendLine(row("REMISE:", String.format(Locale.US, "-%.2f %s", order.discountAmount, company.currencySymbol)))
        }
        sb.appendLine(row("TOTAL:", String.format(Locale.US, "%.2f %s", order.finalAmount, company.currencySymbol)))
        sb.appendLine(divider)

        val remaining = (order.finalAmount - order.paidAmount).coerceAtLeast(0.0)
        sb.appendLine(row("MONTANT PAYÉ:", String.format(Locale.US, "%.2f %s", order.paidAmount, company.currencySymbol)))
        sb.appendLine(row("RESTE À PAYER:", String.format(Locale.US, "%.2f %s", remaining, company.currencySymbol)))

        val latestPayment = payments.firstOrNull { it.orderId == order.id }
        val methodStr = latestPayment?.paymentMethod ?: if (order.paidAmount > 0) "Espèces" else "Non réglé"
        sb.appendLine(row("MODE PAIEMENT:", methodStr))
        sb.appendLine(row("STATUT:", order.paymentStatus))
        sb.appendLine(row("OPÉRATEUR:", order.employeeName))

        sb.appendLine(doubleDivider)
        sb.appendLine(center("Merci pour votre confiance !"))
        if (company.receiptFooterNote.isNotBlank()) {
            sb.appendLine(center(company.receiptFooterNote))
        }
        sb.appendLine(center("*** TICKET POS THERMIQUE ***"))
        sb.appendLine(doubleDivider)

        return sb.toString()
    }

    fun printThermalReceipt(
        context: Context,
        order: WashOrder,
        items: List<WashOrderItem>,
        payments: List<Payment>,
        company: Company
    ) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "Facture_${order.orderNumber}"

        val textContent = generateThermalText(order, items, payments, company, company.receiptPaperWidth)

        printManager.print(
            jobName,
            object : PrintDocumentAdapter() {
                private var pdfDocument: PdfDocument? = null

                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: Bundle?
                ) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onLayoutCancelled()
                        return
                    }

                    val pdi = PrintDocumentInfo.Builder(jobName)
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build()

                    callback?.onLayoutFinished(pdi, true)
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    val lines = textContent.split("\n")
                    val paint = Paint().apply {
                        color = Color.BLACK
                        textSize = 9.5f
                        typeface = Typeface.MONOSPACE
                        isAntiAlias = true
                    }

                    val lineHeight = 14f
                    val pageHeight = (lines.size * lineHeight + 80).toInt().coerceAtLeast(400)
                    val pageWidth = if (company.receiptPaperWidth == "58mm") 230 else 300 // points approx for 80mm

                    val doc = PdfDocument()
                    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
                    val page = doc.startPage(pageInfo)
                    val canvas: Canvas = page.canvas

                    canvas.drawColor(Color.WHITE)

                    var y = 24f
                    for (line in lines) {
                        canvas.drawText(line, 12f, y, paint)
                        y += lineHeight
                    }

                    doc.finishPage(page)

                    try {
                        destination?.fileDescriptor?.let { fd ->
                            FileOutputStream(fd).use { out ->
                                doc.writeTo(out)
                            }
                        }
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    } finally {
                        doc.close()
                    }
                }
            },
            PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                .build()
        )
    }
}
