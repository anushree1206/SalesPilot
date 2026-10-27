package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        BusinessProfileEntity::class,
        ProductEntity::class,
        CustomerEntity::class,
        LeadEntity::class,
        OrderEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class SalesPilotDatabase : RoomDatabase() {
    abstract fun businessProfileDao(): BusinessProfileDao
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun leadDao(): LeadDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: SalesPilotDatabase? = null

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE business_profile ADD COLUMN isCodEnabled INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN aiBackendMode TEXT NOT NULL DEFAULT 'LOCAL_RULE_ENGINE'")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN proxyEndpointUrl TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN customApiKey TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val now = System.currentTimeMillis()
                val defaultEnd = now + (14L * 86400000L)
                db.execSQL("ALTER TABLE business_profile ADD COLUMN subscriptionStatus TEXT NOT NULL DEFAULT 'TRIAL_ACTIVE'")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN selectedPlan TEXT NOT NULL DEFAULT 'FREE_TRIAL'")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN trialStartDate INTEGER NOT NULL DEFAULT $now")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN trialEndDate INTEGER NOT NULL DEFAULT $defaultEnd")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN isPilotGranted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN pilotNotes TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Multi-tenant businessId columns with safe default
                db.execSQL("ALTER TABLE products ADD COLUMN businessId TEXT NOT NULL DEFAULT 'primary_business_profile'")
                db.execSQL("ALTER TABLE customers ADD COLUMN businessId TEXT NOT NULL DEFAULT 'primary_business_profile'")
                db.execSQL("ALTER TABLE leads ADD COLUMN businessId TEXT NOT NULL DEFAULT 'primary_business_profile'")
                db.execSQL("ALTER TABLE orders ADD COLUMN businessId TEXT NOT NULL DEFAULT 'primary_business_profile'")

                // Order payment separation & balance tracking columns
                db.execSQL("ALTER TABLE orders ADD COLUMN paymentStatus TEXT NOT NULL DEFAULT 'PENDING'")
                db.execSQL("ALTER TABLE orders ADD COLUMN amountPaid REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE orders ADD COLUMN paymentReference TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE orders ADD COLUMN paymentNotes TEXT NOT NULL DEFAULT ''")

                // Business profile extra fields for registration & authentication
                db.execSQL("ALTER TABLE business_profile ADD COLUMN ownerEmail TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN shopAddress TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN isOnlineAccountVerified INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN authUserId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE business_profile ADD COLUMN accountType TEXT NOT NULL DEFAULT 'LOCAL_DEVICE'")
            }
        }

        fun getDatabase(context: Context): SalesPilotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalesPilotDatabase::class.java,
                    "salespilot_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseSeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseSeedCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed initial boutique inventory and profile so the user can immediately test
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedInitialData(database)
                    }
                }
            }
        }

        suspend fun seedInitialData(database: SalesPilotDatabase) {
            val profileDao = database.businessProfileDao()
            val productDao = database.productDao()
            val customerDao = database.customerDao()
            val leadDao = database.leadDao()
            val orderDao = database.orderDao()

            // 1. Business Profile
            val profile = BusinessProfileEntity()
            profileDao.insertOrUpdate(profile)

            // 2. Verified Indian Boutique Catalog
            val p1 = ProductEntity(
                id = "PROD-KJV-01",
                name = "Pure Kanjivaram Silk Saree - Crimson Maroon & Gold",
                sku = "KJV-01",
                category = "Sarees",
                price = 4499.00,
                salePrice = 3999.00,
                description = "Handwoven pure silk Kanjivaram saree with rich contrast gold zari pallu and temple border. Comes with unstitched running blouse piece.",
                fabricOrMaterial = "Pure Mulberry Silk with High-Grade Gold Zari",
                availableSizes = "Free Size (6.3m with blouse)",
                stockQuantity = 6,
                isAvailable = true,
                tags = "Bridal, Festive, Pure Silk, Handloom"
            )
            val p2 = ProductEntity(
                id = "PROD-JWL-02",
                name = "Kundan & Temple Pearl Choker Set",
                sku = "JWL-02",
                category = "Jewellery",
                price = 1899.00,
                salePrice = 1599.00,
                description = "Antique matte gold finished choker necklace embellished with premium kundan stones and hanging south sea pearls. Includes matching jhumkas.",
                fabricOrMaterial = "Brass Alloy with 24K Micron Gold Plating & Hand-set Stones",
                availableSizes = "Adjustable Dori",
                stockQuantity = 12,
                isAvailable = true,
                tags = "Antique, Temple Jewellery, Party Wear"
            )
            val p3 = ProductEntity(
                id = "PROD-KUR-03",
                name = "Chanderi Silk Anarkali Suit Set with Organza Dupatta",
                sku = "KUR-03",
                category = "Kurtas & Suits",
                price = 2899.00,
                salePrice = 2499.00,
                description = "Pastel mint green pure Chanderi silk stitched anarkali paired with comfortable pants and hand-painted organza floral dupatta with gota patti detailing.",
                fabricOrMaterial = "Chanderi Silk, Soft Cotton Mul Lining, Pure Organza",
                availableSizes = "S (36), M (38), L (40), XL (42)",
                stockQuantity = 4,
                isAvailable = true,
                tags = "Haldi, Sangeet, Pastel, Festive"
            )
            val p4 = ProductEntity(
                id = "PROD-BAN-04",
                name = "Banarasi Georgette Bandhani Saree - Royal Blue",
                sku = "BAN-04",
                category = "Sarees",
                price = 3299.00,
                salePrice = null,
                description = "Traditional Kutchi Bandhej on pure Banarasi lightweight georgette with woven kadwa zari border.",
                fabricOrMaterial = "Pure Georgette with Woven Silver Zari",
                availableSizes = "Free Size (5.5m + 0.8m blouse)",
                stockQuantity = 0, // Out of stock to test AI stock handling!
                isAvailable = false,
                tags = "Bandhani, Lightweight, Festive"
            )

            productDao.insertProduct(p1)
            productDao.insertProduct(p2)
            productDao.insertProduct(p3)
            productDao.insertProduct(p4)

            // 3. Customers
            val c1 = CustomerEntity(
                id = "CUST-01",
                name = "Pooja Hegde",
                phone = "+91 98451 22334",
                preferredLanguage = "kn",
                source = "Instagram DM",
                city = "Bangalore",
                notes = "Looking for cousin wedding outfit. Prefers Kannada communication.",
                totalOrdersCount = 1,
                totalSpend = 3999.0
            )
            val c2 = CustomerEntity(
                id = "CUST-02",
                name = "Ananya Sharma",
                phone = "+91 98112 33445",
                preferredLanguage = "hi",
                source = "WhatsApp",
                city = "Delhi",
                notes = "Asked if COD is available for Diwali delivery.",
                totalOrdersCount = 0,
                totalSpend = 0.0
            )
            val c3 = CustomerEntity(
                id = "CUST-03",
                name = "Rukmini Iyer",
                phone = "+91 94480 99887",
                preferredLanguage = "en",
                source = "WhatsApp",
                city = "Chennai",
                notes = "Frequent buyer. Loves temple jewellery sets.",
                totalOrdersCount = 2,
                totalSpend = 4798.0
            )

            customerDao.insertCustomer(c1)
            customerDao.insertCustomer(c2)
            customerDao.insertCustomer(c3)

            // 4. Leads
            val l1 = LeadEntity(
                id = "LEAD-101",
                customerId = c2.id,
                customerName = c2.name,
                customerPhone = c2.phone,
                stage = LeadStage.NEW_ENQUIRY,
                productId = p1.id,
                productName = p1.name,
                inquiryText = "Kya ye Kanjivaram saree Delhi me Friday tak deliver ho sakti hai? Aur COD option hai kya?",
                lastAiDraftReply = null,
                lastDraftLanguage = "hi",
                followUpDate = System.currentTimeMillis() + (86400000L * 1), // Tomorrow
                followUpNote = "Confirm dispatch address & ₹100 COD advance deposit.",
                estimatedValue = 3999.00,
                source = "WhatsApp"
            )
            val l2 = LeadEntity(
                id = "LEAD-102",
                customerId = c1.id,
                customerName = c1.name,
                customerPhone = c1.phone,
                stage = LeadStage.FOLLOW_UP_DUE,
                productId = p3.id,
                productName = p3.name,
                inquiryText = "ನಮಸ್ಕಾರ, Chanderi Anarkali size M (38) ಸ್ಟಾಕ್ ಇದೆಯಾ? ಬೆಂಗಳೂರಿಗೆ ಡೆಲಿವರಿ ಚಾರ್ಜ್ ಎಷ್ಟು?",
                lastAiDraftReply = "ನಮಸ್ಕಾರ ಪೂಜಾ ಅವರೇ! ಹೌದು, Chanderi Silk Anarkali Suit Set Size M (38) ಪ್ರಸ್ತುತ ನಮ್ಮಲ್ಲಿ ಲಭ್ಯವಿದೆ. ಇದರ ರಿಯಾಯಿತಿ ದರ ₹2,499. ನಮ್ಮ ಪಾಲಿಸಿ ಪ್ರಕಾರ ₹1,499 ಮೇಲ್ಪಟ್ಟ ಆರ್ಡರ್‌ಗಳಿಗೆ ಬೆಂಗಳೂರಿಗೆ ಉಚಿತ ಶಿಪ್ಪಿಂಗ್ ಇರುತ್ತದೆ! ನಿಮಗೆ ಆರ್ಡರ್ ಕನ್ಫರ್ಮ್ ಮಾಡಬೇಕೇ?",
                lastDraftLanguage = "kn",
                followUpDate = System.currentTimeMillis() - 3600000L, // Overdue by 1 hour
                followUpNote = "Hold M size until today evening 6 PM.",
                estimatedValue = 2499.00,
                source = "Instagram DM"
            )

            leadDao.insertLead(l1)
            leadDao.insertLead(l2)

            // 5. Initial Order
            val ord1 = OrderEntity(
                id = "ORD-2026-101",
                customerId = c1.id,
                customerName = c1.name,
                customerPhone = c1.phone,
                productId = p1.id,
                productName = p1.name,
                quantity = 1,
                unitPrice = 3999.00,
                shippingFee = 0.0,
                discountAmount = 0.0,
                totalAmount = 3999.00,
                status = OrderStatus.DELIVERED,
                deliveryAddress = "#42, 4th Cross, Malleshwaram, Bangalore - 560003",
                paymentMethod = "UPI (GPay)",
                isPaid = true,
                trackingNumber = "DTDC-BLR-88392"
            )
            orderDao.insertOrder(ord1)
        }
    }
}
