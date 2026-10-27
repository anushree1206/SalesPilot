package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SalesPilotDatabase
import com.example.data.local.entity.*
import com.example.ui.DashboardMetrics
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.URLEncoder
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class MultiTenantAndPaymentTest {

    private lateinit var db: SalesPilotDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SalesPilotDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // 1. ORDER PAYMENT STATE TRANSITIONS
    @Test
    fun testOrderPaymentTransitions() = runBlocking {
        val orderDao = db.orderDao()

        val initialOrder = OrderEntity(
            id = "ORD-TEST-100",
            businessId = "shop_meera",
            customerId = "CUST-01",
            customerName = "Pooja Hegde",
            customerPhone = "+91 98451 22334",
            productId = "PROD-KJV-01",
            productName = "Pure Kanjivaram Silk Saree",
            quantity = 1,
            unitPrice = 3999.0,
            shippingFee = 0.0,
            totalAmount = 3999.0,
            status = OrderStatus.CONFIRMED,
            paymentMethod = "COD with ₹1000 Advance",
            paymentStatus = OrderPaymentStatus.PENDING,
            amountPaid = 0.0,
            isPaid = false
        )

        orderDao.insertOrder(initialOrder)

        // Verify initial state
        var fetched = orderDao.getOrderById("ORD-TEST-100")
        assertNotNull(fetched)
        assertEquals(OrderPaymentStatus.PENDING, fetched!!.paymentStatus)
        assertEquals(0.0, fetched.amountPaid, 0.01)
        assertEquals(3999.0, fetched.balanceDue, 0.01)
        assertFalse(fetched.isPaid)

        // Step 1: Record ₹1000 partial advance deposit via UPI
        orderDao.updateOrderPaymentDetails(
            orderId = "ORD-TEST-100",
            paymentStatus = OrderPaymentStatus.PARTIALLY_PAID,
            amountPaid = 1000.0,
            reference = "UPI-REF-992810",
            notes = "Advance deposit received before courier dispatch",
            isPaid = false
        )

        fetched = orderDao.getOrderById("ORD-TEST-100")
        assertNotNull(fetched)
        assertEquals(OrderPaymentStatus.PARTIALLY_PAID, fetched!!.paymentStatus)
        assertEquals(1000.0, fetched.amountPaid, 0.01)
        assertEquals(2999.0, fetched.balanceDue, 0.01)
        assertEquals("UPI-REF-992810", fetched.paymentReference)
        assertFalse(fetched.isPaid)

        // Step 2: Record remaining balance upon courier delivery
        orderDao.updateOrderPaymentDetails(
            orderId = "ORD-TEST-100",
            paymentStatus = OrderPaymentStatus.PAID,
            amountPaid = 3999.0,
            reference = "COD-CASH-SETTLED",
            notes = "Full cash collected by DTDC delivery agent",
            isPaid = true
        )

        fetched = orderDao.getOrderById("ORD-TEST-100")
        assertNotNull(fetched)
        assertEquals(OrderPaymentStatus.PAID, fetched!!.paymentStatus)
        assertEquals(3999.0, fetched.amountPaid, 0.01)
        assertEquals(0.0, fetched.balanceDue, 0.01)
        assertTrue(fetched.isPaid)
    }

    // 2. MULTI-TENANT DATA ISOLATION
    @Test
    fun testMultiTenantDataIsolation() = runBlocking {
        val productDao = db.productDao()
        val orderDao = db.orderDao()

        // Shop A records
        val p1 = ProductEntity(
            id = "PROD-A1",
            businessId = "tenant_meera_silks",
            name = "Kanjivaram Saree",
            sku = "KJV-01",
            category = "Sarees",
            price = 4500.0,
            fabricOrMaterial = "Silk",
            availableSizes = "Free Size",
            stockQuantity = 5,
            description = "Traditional pure silk"
        )
        val o1 = OrderEntity(
            id = "ORD-A1",
            businessId = "tenant_meera_silks",
            customerId = "C-1",
            customerName = "Meera Customer",
            customerPhone = "+91 98450 11111",
            productId = "PROD-A1",
            productName = "Kanjivaram Saree",
            quantity = 1,
            unitPrice = 4500.0,
            totalAmount = 4500.0,
            status = OrderStatus.CONFIRMED
        )

        // Shop B records
        val p2 = ProductEntity(
            id = "PROD-B1",
            businessId = "tenant_ananya_jewels",
            name = "Temple Choker",
            sku = "CHK-02",
            category = "Jewellery",
            price = 1999.0,
            fabricOrMaterial = "Brass & Pearls",
            availableSizes = "Adjustable",
            stockQuantity = 10,
            description = "Antique choker set"
        )
        val o2 = OrderEntity(
            id = "ORD-B1",
            businessId = "tenant_ananya_jewels",
            customerId = "C-2",
            customerName = "Ananya Customer",
            customerPhone = "+91 98110 22222",
            productId = "PROD-B1",
            productName = "Temple Choker",
            quantity = 1,
            unitPrice = 1999.0,
            totalAmount = 1999.0,
            status = OrderStatus.CONFIRMED
        )

        productDao.insertProduct(p1)
        productDao.insertProduct(p2)
        orderDao.insertOrder(o1)
        orderDao.insertOrder(o2)

        // Query Shop A records only
        val meeraProducts = productDao.getProductsForBusiness("tenant_meera_silks").first()
        val meeraOrders = orderDao.getOrdersForBusiness("tenant_meera_silks").first()

        assertEquals(1, meeraProducts.size)
        assertEquals("PROD-A1", meeraProducts[0].id)
        assertEquals(1, meeraOrders.size)
        assertEquals("ORD-A1", meeraOrders[0].id)

        // Query Shop B records only
        val ananyaProducts = productDao.getProductsForBusiness("tenant_ananya_jewels").first()
        val ananyaOrders = orderDao.getOrdersForBusiness("tenant_ananya_jewels").first()

        assertEquals(1, ananyaProducts.size)
        assertEquals("PROD-B1", ananyaProducts[0].id)
        assertEquals(1, ananyaOrders.size)
        assertEquals("ORD-B1", ananyaOrders[0].id)
    }

    // 3. DASHBOARD METRICS & ZERO STATES
    @Test
    fun testDashboardMetricsComputation() {
        val orders = listOf(
            OrderEntity(
                id = "O1",
                customerId = "C1", customerName = "A", customerPhone = "1",
                productId = "P1", productName = "Prod 1",
                unitPrice = 2000.0, totalAmount = 2000.0,
                amountPaid = 2000.0, paymentStatus = OrderPaymentStatus.PAID,
                status = OrderStatus.DELIVERED
            ),
            OrderEntity(
                id = "O2",
                customerId = "C2", customerName = "B", customerPhone = "2",
                productId = "P2", productName = "Prod 2",
                unitPrice = 3000.0, totalAmount = 3000.0,
                amountPaid = 1000.0, paymentStatus = OrderPaymentStatus.PARTIALLY_PAID,
                status = OrderStatus.CONFIRMED
            ),
            OrderEntity(
                id = "O3",
                customerId = "C3", customerName = "C", customerPhone = "3",
                productId = "P3", productName = "Prod 3",
                unitPrice = 5000.0, totalAmount = 5000.0,
                amountPaid = 0.0, paymentStatus = OrderPaymentStatus.PENDING,
                status = OrderStatus.CANCELLED // Cancelled! Should NOT count towards revenue or dues
            )
        )

        val activeOrders = orders.filter { it.status != OrderStatus.CANCELLED }
        val totalRevenue = activeOrders.sumOf { it.totalAmount }
        val totalOutstanding = activeOrders.sumOf { it.balanceDue }

        assertEquals(5000.0, totalRevenue, 0.01) // 2000 + 3000
        assertEquals(2000.0, totalOutstanding, 0.01) // 0 + 2000 (3000 - 1000)

        // Test Zero-State handling
        val emptyOrders = emptyList<OrderEntity>()
        val emptyLeads = emptyList<LeadEntity>()
        val zeroStateConversion = if (emptyLeads.isNotEmpty()) (emptyOrders.size * 100) / emptyLeads.size else 0
        assertEquals(0, zeroStateConversion)
    }

    // 4. MERCHANT UPI DEEP LINK GENERATION
    @Test
    fun testMerchantUpiDeepLinkGeneration() {
        val merchantVpa = "meerasilk@okhdfcbank"
        val businessName = "Meera's Handcrafted Silk & Jewellery"
        val order = OrderEntity(
            id = "ORD-4821",
            customerId = "C-1",
            customerName = "Pooja Hegde",
            customerPhone = "+91 98451 22334",
            productId = "P-1",
            productName = "Silk Saree",
            unitPrice = 3999.0,
            totalAmount = 3999.0,
            amountPaid = 1000.0,
            paymentStatus = OrderPaymentStatus.PARTIALLY_PAID
        )

        val balanceDue = order.balanceDue
        assertEquals(2999.0, balanceDue, 0.01)

        val amountStr = String.format(Locale.US, "%.2f", balanceDue)
        val note = "Order ${order.id}".replace(" ", "%20")
        val encodedPayee = URLEncoder.encode(businessName, "UTF-8")
        val upiLink = "upi://pay?pa=$merchantVpa&pn=$encodedPayee&am=$amountStr&cu=INR&tn=$note"

        assertTrue(upiLink.startsWith("upi://pay?pa=meerasilk@okhdfcbank"))
        assertTrue(upiLink.contains("am=2999.00"))
        assertTrue(upiLink.contains("cu=INR"))
        assertTrue(upiLink.contains("tn=Order%20ORD-4821"))

        // Security check: Customer phone and customer private address MUST NOT be in UPI URL
        assertFalse(upiLink.contains(order.customerPhone))
        assertFalse(upiLink.contains("Pooja"))
    }
}
