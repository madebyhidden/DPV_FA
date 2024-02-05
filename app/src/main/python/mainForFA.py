import os
import subprocess

from datetime import datetime, timedelta

# subprocess.call(['pip', 'install', '-r', 'requirements.txt'])


import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

null_dull = {'auditorium': '', 'beginLesson': '', 'building': '', 'date': '', 'discipline': '', 'endLesson': '',
             'kindOfWork': '',
             'dayOfWeekString': ''}

# Названия направлений
directions = ['Модуль "ERP-системы"', 'Модуль "Системное программирование"', 'Модуль "Управление разработкой"',
              'Модуль "Технологии искусственного интеллекта"', 'Модуль "Языки и методы программирования"',
              'Модуль "Разработка распределенных приложений"',
              'Модуль "Технологии машинного обучения"', 'Модуль "Финтех"']

group_ = {'ПИ21–1': 110687, 'ПИ21–2': 110809, 'ПИ21–3': 110811, 'ПИ21–4': 110812, 'ПИ21–5': 110813, 'ПИ21–6': 110814,
          'ПИ21–7': 110815}

specializations = {
    "Модуль ERP-системы": [
        "Разработка корпоративных и облачных приложений",
        "Корпоративные информационные системы",
    ],
    "Модуль Системное программирование": [
        "Разработка эффективных вычислительных алгоритмов",
        "Низкоуровневое программирование",
    ],
    "Модуль Управление разработкой": [
        "Управление качеством программных систем",
        "Проектирование информационных систем",
    ],
    "Модуль Технологии искусственного интеллекта": [
        "Технологии и алгоритмы анализа сетевых моделей",
    ],
    "Модуль Языки и методы программирования": [
        "Программирование в среде R",
    ],
    "Модуль Разработка распределенных приложений": [
        "Основы технологий интернета вещей",
    ],
    "Модуль Технологии машинного обучения": [
        "Оптимизационные задачи в машинном обучении",
    ],
    "Модуль Финтех": [
        "Теоретические основы финансовых технологий",
    ],
}


basic_disciplines = ['Иностранный язык в профессиональной сфере', 'Информационное право',
                     'Машинное обучение в семантическом и сетевом анализе', 'Программная инженерия',
                     'Бухгалтерские информационные системы']

# Список дисциплин пользователя (по умолчанию базовые дисциплины)
user_disciplines = basic_disciplines.copy()

def get_from_studio(name1, name2):
    a = name1+ " KKK  " +  name2
    return a

#
#
def get_schedule(group, start_date, finish_date):
    url = f'https://ruz.fa.ru/api/schedule/group/{group}?start={start_date}&finish={finish_date}&lng=1'
    schedule_data = requests.get(url).json()

    return schedule_data
#
#
def calculate_end_date(start_date):
    start_datetime = datetime.strptime(start_date, '%Y.%m.%d')
    days_until_sunday = (6 - start_datetime.weekday()) % 7
    end_datetime = start_datetime + timedelta(days=days_until_sunday)
    return end_datetime.strftime('%Y.%m.%d')


def pretty_print_schedule(group, matches, count=0):
    if matches:

        result_text = ''
        header = f"{'Date':<16} {'Type':<39}  {'Discipline':<55} {'Time':<19} {'Auditorium':<15} \n"
        result_text += header
        result_text += '-' * len(header) + '\n'
        if group in ['ПИ21–5', 'ПИ21–6', 'ПИ21–7']:
            for match in matches:
                if match['auditorium'] == 'ЛП51_1/0614':
                    continue

                row = f"{match['date']:<1} {match['dayOfWeekString']:<5} {match['kindOfWork']:<40} {match['discipline']:<55} {match['beginLesson']} - {match['endLesson']:<11} {match['auditorium']:<15}\n"
                result_text += row
            return result_text
        elif group in ['ПИ21–1', 'ПИ21–2']:
            booling = False
            for match in matches:

                if match['discipline'] == 'Иностранный язык в профессиональной сфере':
                    count += 1
                row = f"{match['date']:<1} {match['dayOfWeekString']:<5} {match['kindOfWork']:<40} {match['discipline']:<55} {match['beginLesson']} - {match['endLesson']:<11} {match['auditorium']:<15}\n"
                result_text += row
            return result_text
        elif group in ['ПИ21–3', 'ПИ21–4']:
            booling = False
            for match in matches:

                if match['discipline'] == 'Иностранный язык в профессиональной сфере':
                    count += 1
                row = f"{match['date']:<1} {match['dayOfWeekString']:<5} {match['kindOfWork']:<40} {match['discipline']:<55} {match['beginLesson']} - {match['endLesson']:<11} {match['auditorium']:<15}\n"
                result_text += row

            return result_text
    else:
        return 'Расписание не найдено.'
#
#
#
# def pretty_print_schedule( matches, count=0):
#
#     if matches:
#
#         result_text = ''
#         header = f"{'Date':<16} {'Type':<39}  {'Discipline':<55} {'Time':<19} {'Auditorium':<15} \n"
#         result_text += header
#         result_text += '-' * len(header) + '\n'
#         if combobox_group.get() in ['ПИ21-5', 'ПИ21-6', 'ПИ21-7']:
#             for match in matches:
#                 if match['auditorium'] == 'ЛП51_1/0614':
#                     continue
#                 if count == 0 or count == 5:
#                     if count == 0:
#                         row = f"{match['date']:<1} {match['dayOfWeekString']:<5} {match['kindOfWork']:<40} {match['discipline']:<55} {match['beginLesson']} - {match['endLesson']:<11} {'Персон. Ауд.':<15}\n"
#                         result_text += row
#                     else:
#                         row = f"{match['date']:<1} {match['dayOfWeekString']:<5} {match['kindOfWork']:<40} {match['discipline']:<55} {match['beginLesson']} - {match['endLesson']:<11} {match['auditorium']:<15}\n"
#                         result_text += row
#                 if match['discipline'] == 'Иностранный язык в профессиональной сфере':
#                     count += 1
#
#             return result_text
#         elif combobox_group.get() in ['ПИ21-1', 'ПИ21-2']:
#             booling = False
#             for match in matches:
#
#                 if match['discipline'] == 'Иностранный язык в профессиональной сфере':
#                     count += 1
#                 if count == 1:
#                     row = f"{match['date']:<1} {match['dayOfWeekString']:<5} {match['kindOfWork']:<40} {match['discipline']:<55} {match['beginLesson']} - {match['endLesson']:<11} {'Персон. Ауд.':<15}\n"
#                     result_text += row
#                     booling = True
#                 elif count == 0 or count == 4:
#                     row = f"{match['date']:<1} {match['dayOfWeekString']:<5} {match['kindOfWork']:<40} {match['discipline']:<55} {match['beginLesson']} - {match['endLesson']:<11} {match['auditorium']:<15}\n"
#                     result_text += row
#                 if count == 3:
#                     count += 1
#             return result_text
#         elif combobox_group.get() in ['ПИ21-3', 'ПИ21-4']:
#             booling = False
#             for match in matches:
#
#                 if match['discipline'] == 'Иностранный язык в профессиональной сфере':
#                     count += 1
#                 if count == 1:
#                     row = f"{match['date']:<1} {match['dayOfWeekString']:<5} {match['kindOfWork']:<40} {match['discipline']:<55} {match['beginLesson']} - {match['endLesson']:<11} {'Персон. Ауд.':<15}\n"
#                     result_text += row
#                     booling = True
#                 elif count == 0 or count == 4:
#                     row = f"{match['date']:<1} {match['dayOfWeekString']:<5} {match['kindOfWork']:<40} {match['discipline']:<55} {match['beginLesson']} - {match['endLesson']:<11} {match['auditorium']:<15}\n"
#                     result_text += row
#                 if count == 3:
#                     count += 1
#             return result_text
#     else:
#         return 'Расписание не найдено.'
# #
#
def find_classes_for_date_and_disciplines(disciplines, schedule):
    matches = []

    for class_info in schedule:
        if class_info['discipline'] in disciplines:
            matches.append(class_info)
    return matches
#
#
def update_disciplines(name1, name2):



    selected_direction = name1

    selected_specialization = name2
    if selected_direction == selected_specialization:
        return  'Ошибка: Направления должны быть разными!'
    # Очищаем и обновляем список дисциплин пользователя
    user_disciplines.clear()
    user_disciplines.extend(basic_disciplines)

    # Добавляем дисциплины для выбранного направления и специализации
    user_disciplines.extend(specializations.get(selected_direction, []))
    user_disciplines.extend(specializations.get(selected_specialization, []))
    return user_disciplines


#
def update_schedule(group, start_date):
    finish_date = calculate_end_date(start_date)
    schedule_data = get_schedule(group_[group], start_date, finish_date)
    matching_classes = find_classes_for_date_and_disciplines(user_disciplines, schedule_data)
    return pretty_print_schedule(group, matching_classes)
#
#
# # Tkinter GUI
# root = tk.Tk()
# root.title('Расписание занятий')
#
# # Создаем первый выпадающий список направлений
# label_direction00 = tk.Label(root, text='Выберите группу:')
# label_direction00.pack()
#
# combobox_group = ttk.Combobox(root, values=list(group_.keys()))
# combobox_group.set(list(group_.keys())[6])  # Устанавливаем значение по умолчанию
# combobox_group.pack()
#
# # Создаем первый выпадающий список направлений
# label_direction = tk.Label(root, text='Выберите направление:')
# label_direction.pack()
#
# combobox_direction = ttk.Combobox(root, values=directions)
# combobox_direction.bind('<<ComboboxSelected>>', update_disciplines)
# combobox_direction.set(directions[0])  # Устанавливаем значение по умолчанию
# combobox_direction.pack()
#
# # Создаем второй выпадающий список специализаций
# label_specialization = tk.Label(root, text='Выберите направление:')
# label_specialization.pack()
#
# combobox_specialization2 = ttk.Combobox(root, values=directions)
# combobox_specialization2.bind('<<ComboboxSelected>>', update_disciplines)
# combobox_specialization2.set(directions[1])  # Устанавливаем значение по умолчанию
# combobox_specialization2.pack()
#
# error_label = tk.Label(root, text='', fg='red')
# error_label.pack()
#
# # Создаем виджет календаря для выбора начальной даты
# label_start_date = tk.Label(root, text='Дата начала недели (понедельник):')
# label_start_date.pack()
#
# entry_start_date = DateEntry(root, date_pattern='yyyy.mm.dd', year=2024, month=2, day=12)
# entry_start_date.pack()
#
# # Создаем текстовое поле для вывода дисциплин пользователя
# disciplines_text = scrolledtext.ScrolledText(root, width=40, height=8, wrap=tk.WORD)
# disciplines_text.insert(tk.INSERT, '\n')
# disciplines_text.config(state=tk.DISABLED)
# disciplines_text.pack()
#
# # Кнопка для обновления расписания
# update_button = Button(root, text='Обновить расписание', command=update_schedule)
# update_button.pack()
#
#
# def convert_to_ical():
#     cal = Calendar()
#
#     cal.add('version', '2.0')
#     cal.add('prodid', '-//ZContent//ZapCalLib 1.0//EN')
#     cal.add('calscale', 'GREGORIAN')
#     cal.add('method', 'PUBLISH')
#
#     # Добавляем VTIMEZONE
#     timezone = Timezone()
#     timezone.add('tzid', 'Europe/Moscow')
#     cal.add_component(timezone)
#     full_schedule = []
#     booling = False
#     for i in result_text.get("1.0", tk.END).split("\n")[2:]:
#         i = i.split("    ")
#         filtered = list(filter(lambda x: x != "", i))
#         full_schedule.append(filtered)
#     full_schedule = full_schedule[:-2]
#     for schedule in full_schedule:
#
#         date_str, event_type, discipline, time_str, auditorium = schedule
#         date_str = date_str.split(" ")[0]
#
#         start_time_str, end_time_str = map(str.strip, time_str.split('-'))
#
#         date = datetime.strptime(date_str, "%Y.%m.%d")
#         start_time = datetime.strptime(start_time_str, "%H:%M")
#         end_time = datetime.strptime(end_time_str, "%H:%M")
#         event = Event()
#
#         event.add('dtstart', date.replace(hour=start_time.hour, minute=start_time.minute))
#         event.add('dtend', date.replace(hour=end_time.hour, minute=end_time.minute))
#         event.add('summary', f"{discipline} ({event_type})")
#         event.add('location', auditorium)
#
#         cal.add_component(event)
#         booling = True
#
#     ical_data = cal.to_ical()
#     if booling:
#         current_directory = os.path.dirname(os.path.abspath(__file__))
#         succ_label.config(text=f'Файл сохранен по пути...где лежит exe')
#     else:
#         succ_label.config(text="")
#     with open(f"my_schedule{start_date}.ics", "wb") as f:
#         f.write(ical_data)
#
#
# succ_label = tk.Label(root, text='', fg='green')
# succ_label.pack()
#
# # Кнопка для обновления расписания
# update_button = Button(root, text='Сохранить в Календарь', command=convert_to_ical)
# update_button.pack()
#
# # Создаем текстовое поле для вывода информации
# result_text = scrolledtext.ScrolledText(root, width=160, height=20, wrap=tk.WORD)
# result_text.insert(tk.INSERT,
#                    "Выберите направление, специализацию и дату начала недели, затем нажмите 'Обновить расписание'.")
# result_text.config(state=tk.DISABLED)
# result_text.pack()
#
# root.mainloop()
