import ItemSelItem from "./ItemSelItem";

function CartManager(props) {
  const { items, className, total } = props;
  const preClassName =
    "max-sm:hidden bg-gray-100 rounded-lg min-w-36 h-full w-full m-3 flex flex-col";
  const combineClass = [className || "", preClassName].join(" ");
  return (
    <>
      <container className={combineClass}>
        <section className="flex-1">
          {items.map((item, i) => (
            <ItemSelItem key={"cart-" + i} item={item} />
          ))}
        </section>
        <section className="grid grid-cols-2 gap-2 text-nowrap m-2">
          <button className="bg-red-400 p-3 rounded-lg">구매하기</button>
          <button className="bg-gray-500 p-3 rounded-lg">취소</button>
        </section>
      </container>
      <container className="max-sm:flex hidden h-12 bg-gray-100 flex-row w-full px-3">
        <div className="flex flex-row flex-1">
          <span className="flex-1 flex items-center">총 결제 금액</span>
          <span className="flex items-center">{total}원</span>
        </div>
        <div className="h-12 w-12"></div>
      </container>
    </>
  );
}

export default CartManager;
