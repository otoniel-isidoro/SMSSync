package org.addhen.smssync.database;

import org.addhen.smssync.models.Message;
import org.addhen.smssync.tasks.ThreadExecutor;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageDatabaseHelper extends BaseDatabseHelper implements MessageDatabase {

    private static MessageDatabaseHelper INSTANCE;


    private MessageDatabaseHelper(Context context, ThreadExecutor threadExecutor) {
        super(context, threadExecutor);


    }

    public static synchronized MessageDatabaseHelper getInstance(Context context,
            ThreadExecutor threadExecutor) {

        if (INSTANCE == null) {
            INSTANCE = new MessageDatabaseHelper(context, threadExecutor);
        }
        return INSTANCE;
    }

    @Override
    public void put(final List<Message> messages, final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).put(messages);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void put(final Message message, final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final String whereClause = "message_uuid = ?";
                        final String whereArgs[] = {message.getUuid()};
                        ContentValues values = cupboard().withEntity(Message.class).toContentValues(message);
                        int rows = cupboard().withDatabase(getWritableDatabase()).update(Message.class, values, whereClause, whereArgs);
                        if(rows == 0) {
                            cupboard().withDatabase(getWritableDatabase()).put(message);
                        }
                        callback.onFinished(null);

                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    public void update(final Message message, final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final String whereClause = "message_uuid = ?";
                        final String whereArgs[] = {message.getUuid()};
                        ContentValues values = cupboard().withEntity(Message.class).toContentValues(message);
                        cupboard().withDatabase(getWritableDatabase()).update(Message.class, values,
                                whereClause, whereArgs);
                        callback.onFinished(null);

                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    public void updateDeliveryFields(final Message message, final DatabaseCallback<Void> callback ) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final String whereClause = "message_uuid = ?";
                        final String whereArgs[] = {message.getUuid()};
                        ContentValues values = new ContentValues();
                        values.put("message_type", message.getType().name());
                        values.put("delivery_result_message", message.getDeliveryResultMessage());
                        values.put("delivery_result_code", message.getDeliveryResultCode());
                        values.put("status", message.getStatus().name());
                        values.put("retries", message.getRetries());
                        cupboard().withDatabase(getWritableDatabase()).update(Message.class, values,
                                whereClause, whereArgs);
                        callback.onFinished(null);

                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    public void updateSentFields(final Message message, final DatabaseCallback<Void> callback){
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final String whereClause = "message_uuid = ?";
                        final String whereArgs[] = {message.getUuid()};
                        ContentValues values = new ContentValues();
                        values.put("message_type", message.getType().name());
                        values.put("sent_result_message", message.getSentResultMessage());
                        values.put("sent_result_code", message.getSentResultCode());
                        values.put("status", message.getStatus().name());
                        values.put("retries", message.getRetries());
                        cupboard().withDatabase(getWritableDatabase()).update(Message.class, values,
                                whereClause, whereArgs);
                        callback.onFinished(null);

                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void deleteByUuid(final String uuid, final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final String whereClause = "message_uuid = ?";
                        final String whereArgs[] = {uuid};
                        cupboard().withDatabase(getWritableDatabase())
                                .delete(Message.class, whereClause, whereArgs);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void deleteAll(final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).delete(Message.class, null);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void deleteAllSentMessages(final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final String whereClause = "status = ?";
                        final String whereArgs[] = {Message.Status.SENT.name()};
                        cupboard().withDatabase(getWritableDatabase())
                                .delete(Message.class, whereClause, whereArgs);

                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void fetchByType(final Message.Type type,
            final DatabaseCallback<List<Message>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if(!isClosed()) {
                    try {
                        final String whereClause = "message_type = ?";
                        List<Message> messages = cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                                .withSelection(whereClause, type.name()).orderBy("messages_date DESC").list();
                        callback.onFinished(messages);
                    }catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void fetchByStatus(final Message.Status status,
            final DatabaseCallback<List<Message>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if(!isClosed()) {
                    try {
                        final String whereClause = "status = ?";
                        List<Message> messages = cupboard().withDatabase(
                                getReadableDatabase()).query(Message.class)
                                .withSelection(whereClause, status.name()).orderBy(
                                        "messages_date DESC").list();
                        callback.onFinished(messages);
                    }catch(Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void fetchByUuid(final String uuid,
            final DatabaseCallback<Message> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        Message message = fetchByUuidQuery(uuid);
                        callback.onFinished(message);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    public Message fetchByUuid(String uuid) {
        return fetchByUuidQuery(uuid);
    }

    private Message fetchByUuidQuery(String uuid) {

        final String whereClause = "message_uuid = ?";
        return cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                .withSelection(whereClause, uuid).get();

    }

    @Override
    public void fetchByUuids(final List<String> uuids,
            final DatabaseCallback<List<Message>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    List<Message> messages = new ArrayList<>();
                    try {
                        for (String uuid : uuids) {
                            messages.add(fetchByUuidQuery(uuid));
                        }
                        callback.onFinished(messages);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void fetchPending(
            final DatabaseCallback<List<Message>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                try {
                    final List <Message> messages = fetchPendingQuery();
                    if(messages !=null) {
                        callback.onFinished(messages);
                    } else {
                        callback.onError(new Exception());
                    }
                }catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    public List<Message> fetchPending() {
        return fetchPendingQuery();
    }

    private List<Message> fetchPendingQuery() {
        if (!isClosed()) {
            final String whereClause = "status != ?";
            return cupboard().withDatabase(getReadableDatabase()).query(
                    Message.class)
                    .withSelection(whereClause, Message.Status.SENT.name()).orderBy(
                            "messages_date DESC").list();
        }
        return null;
    }

    private int totalPendingQuery() {
        if(!isClosed()) {
            final String whereClause = "status != ?";
            return cupboard().withDatabase(getReadableDatabase()).query(
                    Message.class)
                    .withSelection(whereClause, Message.Status.SENT.name()).list().size();
        }
        return 0;
    }

    public int totalPending() {
        return totalPendingQuery();
    }

    @Override
    public void fetchSent(
            final DatabaseCallback<List<Message>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {

                        List<Message> messages = fetchSentQuery();
                        if(messages!=null) {
                            callback.onFinished(messages);
                        } else {
                            callback.onError(new Exception());
                        }
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    private List<Message> fetchSentQuery() {
        if(!isClosed()) {
            final String whereClause = "status = ?";
           return cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                    .withSelection(whereClause, Message.Status.SENT.name()).orderBy("messages_date DESC").list();
        }
        return null;
    }

    public int sentTotal() {
        List<Message> messages = fetchSentQuery();
        return getSize(messages);
    }

    private int getSize(List<Message> messages) {
        if(messages !=null) {
            return messages.size();
        } else {
            return 0;
        }
    }

    public int pendingTotal() {
        return getSize(fetchPending());
    }

    @Override
    public void fetchAll(final DatabaseCallback<List<Message>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final List<Message> messages = cupboard()
                                .withDatabase(getReadableDatabase()).query(Message.class).list();
                        callback.onFinished(messages);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void fetchByLimit(final int limit,
            final DatabaseCallback<List<Message>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final List<Message> messages = cupboard()
                                .withDatabase(getReadableDatabase()).query(Message.class)
                                .limit(limit).list();

                        callback.onFinished(messages);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    public List<Message> fetchByLimit(final int limit) {
        if (!isClosed()) {
            try {
                return cupboard()
                        .withDatabase(getReadableDatabase()).query(Message.class)
                        .limit(limit).list();

            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}