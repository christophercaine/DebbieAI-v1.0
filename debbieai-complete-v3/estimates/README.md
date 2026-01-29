# Debbie Does Estimates - Module Documentation

## Overview
Professional estimate and quote management for contractors with line items, materials, templates, and PDF generation support.

## Features
- Estimate Management: Create, edit, duplicate, send, approve/reject estimates
- Line Items: Labor, materials, equipment, subcontractor, permits, disposal
- Auto-Calculations: Subtotal, tax, discounts, deposit requirements
- Status Workflow: Draft -> Sent -> Viewed -> Approved/Rejected/Expired -> Converted
- Templates: Reusable estimate templates by job type
- Materials Database: Track materials with pricing and waste factors

## File Structure
```
estimates/
├── data/
│   ├── local/
│   │   ├── Estimate.kt          # Entities
│   │   └── EstimateDao.kt       # DAOs
│   └── repository/
│       └── EstimateRepository.kt
├── viewmodel/
│   ├── EstimateViewModel.kt
│   └── EstimateViewModelFactory.kt
└── ui/
    ├── components/
    │   ├── EstimateCard.kt
    │   └── EstimateFilters.kt
    ├── screens/
    │   ├── EstimateListScreen.kt
    │   ├── EstimateDetailScreen.kt
    │   └── CreateEstimateScreen.kt
    └── navigation/
        └── EstimateNavigation.kt
```

## Estimate Number Format
Auto-generated: E{YEAR}-{NNNN}
Examples: E2026-0001, E2026-0042

## Status Flow
DRAFT -> SENT -> VIEWED -> APPROVED/REJECTED/EXPIRED -> CONVERTED
