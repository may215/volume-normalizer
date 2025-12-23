
# Signing Setup Instructions

To enable automated signed builds via GitHub Actions, you need to generate a keystore and add secrets to your GitHub repository.

## 1. Generate a Keystore

Run the following command in your terminal (locally, **not** in the CI environment) to generate a new keystore file (`keystore.jks`).

**IMPORTANT**: Keep this file safe and back it up. If you lose it, you cannot update your app on the Play Store.

```bash
keytool -genkey -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```
*Replace `my-key-alias` with a name of your choice.*

You will be asked to set a **Keystore Password** and a **Key Password**. Remember these.

## 2. Encode Keystore to Base64

GitHub Actions cannot store binary files as secrets comfortably. Convert it to a Base64 string:

**Mac/Linux:**
```bash
base64 -i keystore.jks -o keystore.txt
```

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("./keystore.jks")) | Out-File -Encoding utf8 keystore.txt
```

Copy the contents of `keystore.txt`.

## 3. Add GitHub Secrets

Go to your GitHub Repository -> **Settings** -> **Secrets and variables** -> **Actions** -> **New repository secret**.

Add the following secrets:

| Secret Name | Value |
| :--- | :--- |
| `KEYSTORE_BASE64` | The content of the `keystore.txt` file you just generated. |
| `KEYSTORE_PASSWORD` | The password you set for the keystore file. |
| `KEY_ALIAS` | The alias you used (e.g., `my-key-alias`). |
| `KEY_PASSWORD` | The password for the specific key alias (usually same as keystore password). |

## 4. Trigger a Release

1.  Push your code changes.
2.  Create a tag starting with `v` (e.g., `v1.0.0`).
    ```bash
    git tag v1.0.0
    git push origin v1.0.0
    ```
3.  Go to the **Actions** tab in GitHub to see the build running.
4.  Once finished, check the **Releases** section for your signed APK and AAB.
