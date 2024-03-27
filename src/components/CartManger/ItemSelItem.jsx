function ItemSelItem({ item, qty, onDelete }) {
  return (
    <div className="grid grid-cols-5 mx-2 my-3">
      <div className="col-span-3">{item.name}</div>
      <div className="col-span-1">{item.qty}</div>
      <div className="col-span-1" onClick={() => onDelete(item)}>
        버튼
      </div>
    </div>
  );
}

export default ItemSelItem;
