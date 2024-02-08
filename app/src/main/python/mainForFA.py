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

directions = []
group_ = {}
specializations = {}
basic_disciplines = []
user_disciplines = []

def PI():
    global directions, group_, specializations, basic_disciplines, user_disciplines
    # Названия направлений
    directions = ['Модуль "ERP-системы"', 'Модуль "Системное программирование"', 'Модуль "Управление разработкой"',
                  'Модуль "Технологии искусственного интеллекта"', 'Модуль "Языки и методы программирования"',
                  'Модуль "Разработка распределенных приложений"',
                  'Модуль "Технологии машинного обучения"', 'Модуль "Финтех"']

    group_ = {'ПИ21-1': 110687, 'ПИ21-2': 110809, 'ПИ21-3': 110811, 'ПИ21-4': 110812, 'ПИ21-5': 110813, 'ПИ21-6': 110814,
              'ПИ21-7': 110815}

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


def TCBM():
    global directions, group_, specializations, basic_disciplines, user_disciplines
    directions = ['Модуль "Цифровая трансформация бизнеса"',
                  'Модуль "Управление цифровыми ресурсами компании"',
                  'Модуль "ИТ-менеджмент"',
                  'Модуль "Технологии анализа данных"']

    group_ = {'ТЦБМ21-1': 111294, 'ТЦБМ21-2': 111295, 'ТЦБМ21-3': 111296, 'ТЦБМ21-4': 111297, 'ТЦБМ21-5': 111298}

    specializations = {
        "Модуль Цифровая трансформация бизнеса": [
            "Технологии анализа, оценки и контроля предпринимательских рисков",
            "Финансовый анализ деятельности высокотехнологичных компаний",
        ],
        "Модуль Управление цифровыми ресурсами компании": [
            "Основы управления ИТ-сервисами",
        ],
        "Модуль ИТ-менеджмент": [
            "Корпоративные информационные системы на базе 1С",
            "Корпоративные информационные системы (финансовый сектор)",
        ],
        "Модуль Технологии анализа данных": [
            "Технологии и алгоритмы анализа сетевых моделей",
        ],
    }

    basic_disciplines = ['Иностранный язык в профессиональной сфере', 'Управление данными предприятия',
                     'Цифровые экосистемы и платформы', 'Технологии искусственного ителекта',
                     'Практикум по цифровым бизнес-моделям']

    # Список дисциплин пользователя (по умолчанию базовые дисциплины)
    user_disciplines = basic_disciplines.copy()


def ITM():
    global directions, group_, specializations, basic_disciplines, user_disciplines

    directions = ['Модуль "КИС для среднего и крупного бизнеса"', 'Модуль "Информационно-аналитические технологии"', 'Модуль "Технологии управления коллективной работой"',
                  'Модуль "Сквозные технологии цифровой экономики"']

    group_ = {'ИТМ21-1': 111289, 'ИТМ21-2': 111290, 'ИТМ21-3': 111291, 'ИТМ21-4': 111292, 'ИТМ21-5': 111293}

    specializations =  {
        "Модуль КИС для среднего и крупного бизнеса": [
            "Корпоративные информационные системы на базе 1С",
            "Корпоративные информационные системы (отраслевые решения)"
        ],
        "Модуль Информационно-аналитические технологии": [
            "Платформы бизнес-аналитики"
        ],
        "Модуль Технологии управления коллективной работой": [
            "Практикум Развитие дизайн-мышления",
            "Информационные технологии разработки корпоративного портала"
        ],
        "Модуль Сквозные технологии цифровой экономики":[
            "Мобильные технологии"
        ],
    }

    basic_disciplines = ['Иностранный язык в профессиональной сфере', 'Управление данными предприятия',
                         'Основы управления ИТ-сервисами', 'Практикум по ИТ-менеджменту',
                        'Информационные технологии цифрового предприятия']

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



def find_classes_for_date_and_disciplines(disciplines, schedule):
    matches = []

    for class_info in schedule:
        if class_info['discipline'] in disciplines:
            matches.append(class_info)
    return matches
#
#
def update_disciplines(name1, name2, direction):

    if direction=="PI": PI()
    if direction=="TCBM": TCBM()
    if direction=="ITM": ITM()


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

    print(group_, " 0 ", specializations, " 0 ", basic_disciplines)
    finish_date = calculate_end_date(start_date)
    schedule_data = get_schedule(group_[group], start_date, finish_date)
    matching_classes = find_classes_for_date_and_disciplines(user_disciplines, schedule_data)
    return  matching_classes


update_disciplines('test', 'tesr2', 'PI')
update_schedule('ПИ21-7', '2024.02.05')

