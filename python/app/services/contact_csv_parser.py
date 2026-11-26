from logging import Logger

from app.models.contact_model import ContactCsvRow


class ContactCsvParser:
    """name,phone,email,group 형식의 CSV를 줄 단위로 읽는다. 값 검증은 하지 않는다."""

    def __init__(self, logger: Logger):
        self.logger = logger

    def parse(self, content: bytes) -> list[ContactCsvRow]:
        """CSV 내용을 행 목록으로 바꾼다. 첫 줄(헤더)과 빈 줄은 건너뛴다."""
        line_list = content.decode("utf-8-sig").splitlines()
        row_list = []
        for line_number, line in enumerate(line_list, start=1):
            if line_number == 1 or not line.strip():
                continue
            row_list.append(self._to_row(line_number, line))
        return row_list

    def _to_row(self, line_number: int, line: str) -> ContactCsvRow:
        """한 줄을 열로 나눈다."""
        column_list = [value.strip() for value in line.rstrip(",").split(",")]
        name, phone, email, group_name = column_list
        return ContactCsvRow(line_number=line_number, name=name, phone=phone, email=email, group_name=group_name)
