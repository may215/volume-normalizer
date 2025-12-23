
# Google Play Store Policy Review Checklist

Before publishing *Volume Normalizer*, review this checklist to ensure compliance with Google Play Policies.

## Critical Permissions Used
This app uses sensitive permissions that require specific declarations.

-   [ ] **RECORD_AUDIO**:
    -   **Usage**: Used to capture internal audio for normalization analysis.
    -   **Disclosure**: Ensure your Privacy Policy explicitly states *why* you record audio.
    -   **Store Listing**: You may need to fill out the "Data Safety" section indicating "Audio Recordings" are collected (even if processed locally).
    -   **Note**: If audio never leaves the device, select "Processed ephemerally" if applicable, but be transparent.

-   [ ] **FOREGROUND_SERVICE_MEDIA_PROJECTION**:
    -   **Usage**: Required to keep the service alive while capturing screen/audio content.
    -   **Requirement**: You must provide a video demonstrating this feature in the Play Console during review.

## General Policy Checklist

### 1. Privacy Policy
-   [ ] A valid Privacy Policy URL must be provided in the Store Listing.
-   [ ] It must be accessible from within the app (e.g., an "About" or "Settings" screen link).

### 2. User Data
-   [ ] **Data Safety Form**: Complete the Data Safety form in Play Console.
    -   Does the app collect data? (Yes, Audio).
    -   Is it encrypted in transit? (N/A if local, but "Yes" is safe standard).
    -   Can users request deletion?

### 3. Content Ratings
-   [ ] Complete the IARC Content Rating questionnaire.
-   [ ] Since this is a utility, it should generally be "Everyone" unless it exposes user-generated content.

### 4. Families Policy
-   [ ] **Target Audience**: If you select "Children included" (<13), strict rules apply.
-   [ ] **Recommendation**: Target 13+ or 18+ to avoid rigorous "Designed for Families" requirements unless necessary.

### 5. Metadata & Impersonation
-   [ ] **App Title**: "Volume Normalizer" is generic but safe. Avoid "Spotify Volume Booster" (Brand abuse).
-   [ ] **Icon**: Ensure you own the rights to the icon artwork.
-   [ ] **Description**: Do not use keyword stuffing (e.g., "Best volume app best loud booster").

## Restricted Content
-   [ ] Ensure the app does not assist in downloading copyrighted content (YouTube terms of service specifically forbid downloading/ripping). Your app *analyzes* audio, which is generally fine, but avoid features that *save* the stream to disk permanently if possible.

## Pre-Launch Report
-   [ ] Upload your AAB to the **Internal Testing** track first.
-   [ ] Review the **Pre-Launch Report** for stability issues and accessibility warnings.
