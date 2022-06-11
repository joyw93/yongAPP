package com.example.hklist;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CustomCalendarView extends LinearLayout
{
    // for logging
    private static final String LOGTAG = "Calendar View";

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();
    private Calendar currentViewDate=Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    private FirebaseDatabase database_calendar=FirebaseDatabase.getInstance();
    private DatabaseReference calRef=database_calendar.getReference("CalendarDB");


    public CustomCalendarView(Context context)
    {
        super(context);
    }

    public CustomCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();

        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomCalendarView);

        try
        {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CustomCalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        }
        finally
        {
            ta.recycle();
        }
    }

    private void assignUiElements()
    {
        // layout is inflated, assign local variables to components
        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
    }

    private void assignClickHandlers()
    {
        // add one month and refresh UI
        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, 1);
                currentViewDate.add(Calendar.MONTH, 1);
                updateCalendar();
                eventHandler.setEvents();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, -1);
                currentViewDate.add(Calendar.MONTH, -1);
                updateCalendar();
                eventHandler.setEvents();
            }
        });

        // long-pressing a day
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id)
            {
                // handle long-press
                if (eventHandler == null)
                    return false;

                eventHandler.onDayLongPress((Date)view.getItemAtPosition(position));
                return true;
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> view, View cell, int position, long id)
            {
                eventHandler.onDayPress((Date)view.getItemAtPosition(position));
            }
        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar()
    {
        updateCalendar(null,null);
    }



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events,HashSet<Date> onClickedDay)
    {
        ArrayList<Date> cells = new ArrayList<>();
        //ArrayList<Calendar> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells, events,onClickedDay));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        txtDate.setText(sdf.format(currentDate.getTime()));

        // set header color according to current season


    }

    private class CalendarAdapter extends ArrayAdapter<Date>
    {
        private HashSet<Date> onClickedDay;
        // days with events
        private HashSet<Date> eventDays;
        // for view inflation
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays,HashSet<Date> onClickedDay)
        {
            super(context, R.layout.control_calendar_day, days);
            this.eventDays = eventDays;
            this.onClickedDay=onClickedDay;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            Date date = getItem(position);
            Calendar cal=Calendar.getInstance();
            cal.setTime(date);

            ViewHolder holder;


            //int day = date.getDate();
            int day  =cal.get(Calendar.DAY_OF_MONTH);
            //int month = date.getMonth();
            int month  =cal.get(Calendar.MONTH);
            //int year = date.getYear();
            int year  =cal.get(Calendar.YEAR);

            // today
            //Date today = new Date();
            Calendar today=Calendar.getInstance();

            // inflate item if it does not exist yet
            if (view == null) {
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);
                holder = new ViewHolder();
                holder.text=(TextView) view.findViewById(R.id.textView);
                holder.imageView=(ImageView)view.findViewById(R.id.imageView);
                view.setTag(holder);
            }

            else
            {
                holder = (ViewHolder) view.getTag();
            }

            // if this day has an event, specify event image
            //view.setBackgroundResource(R.drawable.bg_dragon2);
            if (eventDays != null)
            {
                for (Date eventDate : eventDays)
                {
                    Calendar calTmp=Calendar.getInstance();
                    calTmp.setTime(eventDate);
                    if (calTmp.get(Calendar.DAY_OF_MONTH) == day &&
                            calTmp.get(Calendar.MONTH) == month &&
                            calTmp.get(Calendar.YEAR) == year)
                    {
                        // mark this day for event
                        //view.setBackgroundResource(R.drawable.bg_dragon2);
                        //holder.imageView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        holder.imageView.setImageResource(R.drawable.bg_dragon);
                        break;
                    }
                }
            }

            if (onClickedDay != null)
            {
                for (Date eventDate : onClickedDay)
                {
                    Calendar calTmp=Calendar.getInstance();
                    calTmp.setTime(eventDate);
                    if (calTmp.get(Calendar.DAY_OF_MONTH) == day &&
                            calTmp.get(Calendar.MONTH) == month &&
                            calTmp.get(Calendar.YEAR) == year)
                    {
                        //view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryBackGround));
                        view.setBackground(getResources().getDrawable(R.drawable.calendar_background));
                        //holder.text.setBackground(getResources().getDrawable(R.drawable.calendar_background));
                        break;
                    }
                }
            }

            // clear styling
            holder.text.setTypeface(null, Typeface.NORMAL);
            holder.text.setTextColor(Color.BLACK);

            if (month != currentViewDate.get(Calendar.MONTH) || year != currentViewDate.get(Calendar.YEAR))
            {
                // if this day is outside current month, grey it out
                holder.text.setTextColor(getResources().getColor(R.color.greyed_out));
            }
            else if (day == today.get(Calendar.DAY_OF_MONTH)
                    &&month==today.get(Calendar.MONTH)
                    &&year==today.get(Calendar.YEAR))
            {
                // if it is today, set it to blue/bold
                holder.text.setTypeface(null, Typeface.BOLD);
                holder.text.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            // set text
           // ((TextView)view).setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            holder.text.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));


            return view;
        }
    }

    static public class ViewHolder
    {
        public TextView text;
        public ImageView imageView;

    }

    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    public interface EventHandler
    {
        void onDayLongPress(Date date);
        void onDayPress(Date date);
        void setEvents();
    }
}