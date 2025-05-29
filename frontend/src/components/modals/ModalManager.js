// 모달 컴포넌트는, 일종의 '싱글톤' 형태어야 함
// 같은 컴포넌트가 여러 개 생성될 일이 없으며, 등록해놓고 관리하는 게 적절

// 모달 객체를 저장
const modalList = {};

export async function registerModal(modalObj) 
{
    const requiredKeys = ['name', 'setIsOpen', 'modalClear'];

    for (const key of requiredKeys) { if (!(key in modalObj)) { throw new Error(`registerModal 오류: 필수 필드 '${key}'가 누락되었습니다.`); } }
    if (modalList[modalObj.name]) { throw new Error(`registerModal 오류: '${modalObj.name}' 모달은 이미 등록되어 있습니다.`); }
    modalList[modalObj.name] = modalObj;
}

const modalStack = [];

// 모달 저장, 중복 모달이 있을 경우 제거.
export async function modalPush(modalName)
{
    let modalObj = modalList[modalName];
    if (!modalObj) { throw new Error(`모달 '${modalName}' 이(가) 등록되어 있지 않습니다.`); }

    // modalObj.name과 같은 name을 가진 모달 오브젝트가 배열에 있을 경우, 그것만 제거
    for (let i = modalStack.length - 1; i >= 0; i--) { if (modalStack[i].name === modalObj.name) { modalStack.splice(i, 1); break; } }
    // 열려있는 모달이 있을 경우 숨기기
    if (modalStack.length > 0) { modalStack[modalStack.length-1].setIsOpen(false); }
    // 새로 들어온 모달 푸시
    modalStack.push(modalObj);
    // 새로 들어온 모달 띄우기
    modalObj.setIsOpen(true);
}

// 모달 제거. 모달에 저장된 양식들? 모두 삭제하기.
export async function modalPop()
{
    if (modalStack.length > 0)
    {
        let modal = modalStack.pop();
        modal.setIsOpen(false);
        modal.modalClear();
        if (modalStack.length > 0) { modalStack[modalStack.length-1].setIsOpen(true); }
    }
}