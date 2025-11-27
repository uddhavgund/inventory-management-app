import { useEffect, useState } from "react";

export default function App() {

  const [products, setProducts] = useState([]);
  const [name, setName] = useState("");
  const [qty, setQty] = useState("");
  const [file, setFile] = useState(null);
  const [editImageFile, setEditImageFile] = useState(null);

  const loadProducts = async () => {
    const res = await fetch("http://localhost:8080/api/products");
    setProducts(await res.json());
  };

  useEffect(() => {
    loadProducts();
  }, []);

  const addProduct = async () => {
    if (!file || !name || !qty) {
      alert("Fill name, quantity and select image");
      return;
    }

    const form = new FormData();
    form.append("product", new Blob([JSON.stringify({ name, quantity: qty })], { type: "application/json" }));
    form.append("file", file);

    await fetch("http://localhost:8080/api/products/upload", {
      method: "POST",
      body: form
    });

    setName("");
    setQty("");
    setFile(null);

    loadProducts();
  };

  const updateProduct = async (id) => {
    const newName = prompt("Enter new name:");
    const newQty = prompt("Enter new quantity:");

    if (!newName || !newQty) return;

    await fetch(`http://localhost:8080/api/products/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name: newName, quantity: newQty })
    });

    loadProducts();
  };

  const updateImage = async (id) => {
    if (!editImageFile) {
      alert("Choose an image first");
      return;
    }

    const form = new FormData();
    form.append("file", editImageFile);

    await fetch(`http://localhost:8080/api/products/update-image/${id}`, {
      method: "PUT",
      body: form
    });

    setEditImageFile(null);
    loadProducts();
  };

  const deleteProduct = async (id) => {
    if (!confirm("Delete product?")) return;

    await fetch(`http://localhost:8080/api/products/${id}`, {
      method: "DELETE"
    });

    loadProducts();
  };

  return (
    <div style={{ padding: 20, fontFamily: "Segoe UI" }}>
      <h2>Inventory App</h2>

      <div>
        <input placeholder="Product Name" value={name} onChange={(e) => setName(e.target.value)} />
        <input placeholder="Quantity" value={qty} onChange={(e) => setQty(e.target.value)} />
        <input type="file" onChange={(e) => setFile(e.target.files[0])} />
        <button onClick={addProduct}>Add Product</button>
      </div>

      <ul>
        {products.map((p) => (
          <li key={p.id} style={{ margin: 20 }}>
            <img src={`http://localhost:8080/${p.imagePath}`} width="100" height="100" style={{ objectFit: "cover" }} />
            <div>{p.name} — Stock: {p.quantity}</div>

            <button onClick={() => updateProduct(p.id)}>Edit</button>
            <button onClick={() => deleteProduct(p.id)}>Delete</button>

            <div>
              <input type="file" onChange={(e) => setEditImageFile(e.target.files[0])} />
              <button onClick={() => updateImage(p.id)}>Update Image</button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
