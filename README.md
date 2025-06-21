# 🎬 MovieApp - Android Project

Aplikasi Android untuk menampilkan daftar film dari API eksternal (TMDb).  
Project ini dikembangkan sebagai tugas kuliah.

---

## 📂 Cara Menjalankan Project

1. Buka project ini di **Android Studio**
2. Pastikan file `local.properties` tersedia di root project dan berisi:

   ```properties
   sdk.dir=C:\Users\NAMA_KOMPUTER\AppData\Local\Android\Sdk
   TMDB_API_KEY=your_api_key_here
   ```

3. Jika file tersebut tidak ada, buat file baru bernama `local.properties` dan tambahkan isi di atas sesuai konfigurasi komputer Anda.
4. Jalankan project seperti biasa menggunakan emulator atau device.

---

## 🔐 Tentang API Key

API key disimpan di file `local.properties` dan digunakan di `build.gradle` sebagai:

```gradle
buildConfigField "String", "TMDB_API_KEY", "\"${TMDB_API_KEY}\""
```

File ini **sudah disertakan dalam zip** agar project dapat dijalankan langsung.

---

## 📦 Struktur File

| File / Folder         | Deskripsi                                        |
|------------------------|--------------------------------------------------|
| `app/`                | Folder utama aplikasi Android                    |
| `build.gradle`        | Konfigurasi project dan API key injection        |
| `local.properties`    | Berisi path SDK dan API key                      |
| `README.md`           | Penjelasan singkat cara menjalankan project      |

---

## 🙋‍♂️ Informasi Pengembang

- **Nama**: [Saeful Rohman]  
- **NIM**: [20220810038]  
- **Kelas**: [TINFC-2022-02]  
- **Dosen**: [Sherly Gina Supratman, S.Kom., M.Kom.]

---

## ✅ Catatan Tambahan

- Jika terjadi error seperti `API_KEY not defined`, pastikan file `local.properties` tidak kosong.
- Jangan upload file ini ke GitHub public, karena mengandung API key pribadi.

---

Terima kasih 🙏
