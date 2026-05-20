# 🤖 Makine Öğrenmesi ile Satış Verisi Sınıflandırma

> Kocaeli iline ait satış verilerini KNN ve Decision Tree algoritmalarıyla sınıflandıran, karşılaştırmalı performans analizi sunan makine öğrenmesi projesi.

---

## 📌 Proje Hakkında

Bu projede Kocaeli ilinde gerçekleşen satışlara ait veriler (ürün kategorisi, alıcı cinsiyeti, fiyat vb.) kullanılarak iki farklı makine öğrenmesi algoritması implement edilmiş ve karşılaştırılmıştır. Amaç; verilen test verilerinin doğru kategoriye atanıp atanamadığını ölçmek ve algoritma parametrelerini değerlendirmektir.

---

## 🧠 Kullanılan Algoritmalar

### 🔵 K-En Yakın Komşu (KNN)

Parametrik olmayan, denetimli öğrenme tabanlı bir sınıflandırıcıdır. Tahmin yaparken eğitim verisini saklar ve yeni bir veri noktası için en yakın **K komşuyu** bularak oylama yapar.

**Temel özellikler:**
- Örnek tabanlı öğrenme — eğitimde karmaşık model oluşturmaz
- Ağırlıklı Öklid mesafesi kullanılır
- Önemli değişkenlere yüksek ağırlık atanmıştır
- Bu veri seti için optimum **K = 5** olarak belirlenmiştir

```
FUNCTION PREDICT(testSample):
  distances = []
  FOR each trainSample IN trainingData:
    distance = CALCULATE_DISTANCE(testSample, trainSample)
    distances.append(class + distance)
  distances.sort()
  k_neighbors = distances[:k]
  FOR each neighbor IN k_neighbors:
    weight = 1 / (distance + küçük sabit)
    votes[class] += weight
  RETURN max(votes)
```

---

### 🌳 Karar Ağacı (Decision Tree)

Kök, dallar, iç düğümler ve yaprak düğümlerden oluşan hiyerarşik bir yapıya sahip parametrik olmayan denetimli öğrenme yaklaşımıdır.

**Temel özellikler:**
- Veri, soru-cevap mantığıyla ağaç dallarına ayrılır
- En iyi bölünme **entropy** (bilgi kirliliği) ile ölçülür
- Sayısal özelliklerde eşik (threshold) değeri belirlenir
- Kategorik özelliklerde (GENDER, BRAND) her kategori için ayrı dal oluşturulur
- **maxDepth = 10**, **minSamplesLeaf = 5** ile overfitting engellenir

```
FUNCTION BUILD_TREE(data, depth):
  IF data boş OR depth >= maxDepth OR data < minSamples OR veri saf:
    RETURN Leaf Node (en sık sınıf)
  bestSplit = FIND_BEST_SPLIT(data)
  IF bestSplit yok:
    RETURN Leaf Node
  node.left  = BUILD_TREE(bestSplit.leftData,  depth + 1)
  node.right = BUILD_TREE(bestSplit.rightData, depth + 1)
  RETURN node
```

---

## 📊 Veri Seti ve Normalizasyon

| Özellik | Detay |
|---------|-------|
| Toplam Kayıt | 5.000 |
| Hedef Sınıf | Ürün Kategorisi |
| Eğitim / Test Oranı | %80 / %20 |

### Ön İşleme Adımları

- ✅ **Min-Max Normalizasyonu** — Sayısal değerler 0-1 aralığına indirgendi
- ✅ **Kategorik Kodlama** — `Gender` değerleri 0/1 olarak dönüştürüldü
- ✅ **String → Sayısal** — String değerler `hashCode()` ile 0-99 aralığına sıkıştırıldı
- ✅ **Data Leakage Önleme** — Test verisi yalnızca transform edildi, fit edilmedi

---

## 🛠️ Kullanılan Teknolojiler

| Teknoloji | Kullanım Amacı |
|-----------|----------------|
| `Java` | Ana programlama dili |
| `Swing (MainFrame)` | Grafik kullanıcı arayüzü (GUI) |
| `OOP / Interface` | Soyutlama ve polimorfizm |

---

## 🏗️ Proje Yapısı

```
src/
├── IClassifier.java          # Tüm modellerin uyması gereken arayüz
├── BaseAlgorithm.java        # Ortak algoritma altyapısı (abstract)
├── KNNClassifier.java        # KNN modeli — tahmin ve eğitim
├── DecisionTreeClassifier.java  # DT modeli — ağaç kurma ve tahmin
├── PreProcessor.java         # Veri temizleme ve normalizasyon
├── Evaluator.java            # Model performans ölçümü
└── MainFrame.java            # GUI arayüzü
```

### OOP Tasarımı

```java
public interface IClassifier {
    void train(List<UserRecord> trainingData);
    String predict(UserRecord user);
}
```

- **Soyutlama** — `IClassifier` arayüzü ve `BaseAlgorithm` abstract sınıfı ile sağlandı
- **Polimorfizm** — `IClassifier` referansı üzerinden farklı algoritmalar aynı şekilde çalıştırıldı
- **Kullanıcı seçimi** — Çalışma anında KNN veya DT seçilebilmektedir

---

## 🚀 Kullanım

1. Uygulama açıldığında **"Veri Yükle"** butonuna tıklayarak CSV veri setini seçin.
2. Kullanmak istediğiniz algoritmayı seçin: **KNN** veya **Decision Tree**
3. (KNN için) K değerini girin — önerilen: `K = 5`
4. **"Modeli Çalıştır"** butonuna tıklayın.
5. Sonuçlar ve **Confusion Matrix** ekranda görüntülenir.
6. **"Ağacı Göster"** butonu ile DT ağaç yapısını metinsel olarak inceleyebilirsiniz.

---

## 📈 Deneysel Sonuçlar

| Metrik | KNN | Decision Tree |
|--------|-----|--------------|
| ⏱️ Süre | 335 ms | 1920 ms |
| 🎯 Doğruluk | **%84.9** | %72.1 |

### Yorumlar

- **KNN daha hızlı** çalışmıştır; çünkü eğitimde yalnızca veriyi saklar, model kurmaz.
- **DT daha yavaş** çalışmıştır; ağaç yapısı kurma süreci hesaplama yoğundur.
- **Gıda kategorisinin baskınlığı** veri setinin dengesiz olduğunu göstermekte ve sınıflandırmayı zorlaştırmaktadır.
- DT, BRAND özelliğinde aşırı bölünmeye yol açarak genelleme başarısını düşürmüştür.
- Derinlik 10 ve üzerine çıkınca **overfitting riski** belirginleşmektedir.

### Örnek DT Çıktısı

```
└── BRAND == HAL
    ├── CLASS: MEYVE SEBZE
    └── PRICE <= 4.82
        ├── PRICE <= 1.57
        │   ├── BRAND == F SAFF
        │   │   └── CLASS: KAĞIT / EV
        │   └── BRAND == PINAR
        │       └── CLASS: ET TAVUK / SÜT KAHVALTILIK
        └── ...
```

## 📄 Kaynaklar

- [KNN Algoritması — Medium](https://arslanev.medium.com/makine-öğrenmesi-knn-k-nearest-neighbors-algoritması-bdfb688d7c5f)
- [Decision Tree From Scratch — Medium](https://medium.com/@nirmal1067/decision-tree-from-scratch-5c39498ebe02)
- [Normalizasyon — Medium](https://medium.com/baakademi/normalizasyon-fe8005778f68)
- [Decision Trees — Medium](https://medium.com/@MrBam44/decision-trees-91f61a42c724)

