import ProductManager from "../components/ProductManager";

function Admin({ cartData, data }) {
  return (
    <div className="App h-screen w-screen">
      <section className="grid grid-cols-6">
        <ProductManager data={data} />
      </section>
    </div>
  );
}

export default Admin;
