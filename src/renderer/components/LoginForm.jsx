import * as React from "react";
import ButtonCompo from "./ButtonCompo";
function LoginForm({ setId, setPassword, submit }) {
  function handleKeyDown(e){
    console.log(e);
    if(e.key === 'Enter')
      submit();
  }
  return (
    <section className="flex flex-col justify-center items-center flex-1">
      <div className="bg-gray-300 p-4 rounded-lg">
        <div className="grid grid-cols-2 mb-5">
          <span>login:</span>
          <input
            className="mb-2 rounded-lg"
            onChange={(e) => setId(e.target.value)}
          />
          <span>password:</span>
          <input
            type="password"
            className="rounded-lg"
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <ButtonCompo message="로그인" onClick={submit} onKeyDown={e =>handleKeyDown(e)}/>
      </div>
    </section>
  );
}

export default LoginForm;
