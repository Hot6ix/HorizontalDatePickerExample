# HorizontalDatePickerExample
HorizontalDatePicker is a simple library that can select a date by scroll horizontally.  
Also it automatically loads next month if user scroll from month to another month, this can make user to scroll endless.

* #### Basic
![Image](https://imgur.com/bTnlrOm.png)  

* #### Basic with weekend highlight enabled
![Image](https://imgur.com/mdLJF0Q.png)

* #### Scroll
![Image](https://imgur.com/9H7GDHt.png)

* #### Endless scroll
![Image](https://imgur.com/prDro8R.png)

## Requirement
* Minimum SDK version 16+
* Need Android Studio

## Example

    <com.j.simples.horizontaldatepicker.HorizontalDatePicker
        android:id="@+id/horizontalDatePicker"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:dateTextColor="#aaaaaa"
        app:dateTextSize="20sp"
        app:listItemTextColor="#000000"
        app:selectorStrokeColor="#3d3d3d"
        app:selectorStrokeWidth="3dp"
        app:enableWeekendHighlight="true" />
        
## Layout Attributes

| Attribute | Type | Comment | Default value |
|---|---|---|---|
| dateTextColor | Integer | Set date text color | Color.GRAY |
| dateTextSize | Dimension | Set date text size | 18f |
| listItemTextColor | Color | Set recyclerview item text color | Color.GRAY |
| selectorStrokeColor | Color | Set selector stroke color | Color.GRAY |
| selectorStrokeWidth | Dimension | Set selector stroke width | 5 |
| enableWeekendHighlight | Boolean | Set indicator stroke color | false |

## Function  

| Function | Comment |
|---|---|
| addDateChangedListener(listener: OnDateChangedListener) | Set selected date listener |
| setDateFormat(template: String) | Set custom date format |
| setDate(calendar: Calendar) | Set date using calender |
| enableWeekendHighlight(bool: Boolean) | Get current selected Indicator index |
| setSelectorStrokeColor(color: Int) | Set selector stroke color |
| setSelectorStrokeWidth(size: Int) | Set selector stroke width |
